// 定时刷新ID
var refreshTimer = null;

$(document).ready(function() {
    // 页面加载时获取任务列表和日志
    loadJobs(0);
    loadLogs(0);
    
    // 启动默认的定时刷新
    setRefreshInterval(10000, '10秒');
    
    // 保存任务按钮点击事件
    $('#saveJobBtn').click(function() {
        saveJob();
    });
    
    // 手动刷新按钮事件
    $('#refreshJobsBtn').click(function() {
        var currentJobPage = $('#currentJobPage').val() || 0;
        loadJobs(currentJobPage);
    });
    
    $('#refreshLogsBtn').click(function() {
        var currentLogPage = $('#currentLogPage').val() || 0;
        loadLogs(currentLogPage);
    });
});

// 设置刷新频率
function setRefreshInterval(interval, text) {
    $('#refreshInterval').val(interval);
    
    // 更新显示文本
    $('#jobRefreshText').text(text);
    $('#logRefreshText').text(text);
    
    // 清除现有的定时器
    if (refreshTimer) {
        clearInterval(refreshTimer);
        refreshTimer = null;
    }
    
    // 如果interval大于0，则启动新的定时器
    if (interval > 0) {
        refreshTimer = setInterval(function() {
            var currentJobPage = $('#currentJobPage').val() || 0;
            var currentLogPage = $('#currentLogPage').val() || 0;
            loadJobs(currentJobPage);
            loadLogs(currentLogPage);
        }, interval);
    }
    
    // 阻止事件冒泡，防止下拉菜单立即重新打开
    event.stopPropagation();
    
    // 延迟隐藏下拉菜单，确保选项可以被点击
    setTimeout(function() {
        $('.dropdown-menu').removeClass('show');
        $('.dropdown-toggle').attr('aria-expanded', 'false');
    }, 100);
}

// 加载任务列表
function loadJobs(page) {
    $.ajax({
        url: '/api/jobs/page?page=' + page + '&size=10',
        type: 'GET',
        success: function(response) {
            if (response.success) {
                var tbody = $('#jobsTable tbody');
                tbody.empty();
                
                $.each(response.data, function(index, job) {
                    var statusText = job.status === 1 ? '<span class="badge bg-success">运行中</span>' : '<span class="badge bg-secondary">已暂停</span>';
                    var distributedText = job.distributed ? '<span class="badge bg-primary">分布式</span>' : '<span class="badge bg-info">单机</span>';
                    var row = '<tr>' +
                        '<td>' + (job.id || '') + '</td>' +
                        '<td>' + (job.jobName || '') + '</td>' +
                        '<td>' + (job.jobGroup || '') + '</td>' +
                        '<td>' + (job.cronExpression || '') + '</td>' +
                        '<td>' + (job.beanName || '') + '</td>' +
                        '<td>' + (job.methodName || '') + '</td>' +
                        '<td>' + statusText + '</td>' +
                        '<td>' + distributedText + '</td>' +
                        '<td>' +
                            '<div class="btn-group" role="group">' +
                                (job.status === 1 ? 
                                    '<button type="button" class="btn btn-warning btn-sm" onclick="pauseJob(' + job.id + ')">暂停</button>' :
                                    '<button type="button" class="btn btn-success btn-sm" onclick="resumeJob(' + job.id + ')">恢复</button>') +
                                '<button type="button" class="btn btn-info btn-sm" onclick="editJob(' + job.id + ')">编辑</button>' +
                                '<button type="button" class="btn btn-info btn-sm" onclick="runJob(' + job.id + ')">执行</button>' +
                                '<button type="button" class="btn btn-danger btn-sm" onclick="deleteJob(' + job.id + ')">删除</button>' +
                            '</div>' +
                        '</td>' +
                    '</tr>';
                    tbody.append(row);
                });
                
                // 更新分页信息
                renderJobPagination(response.currentPage, response.totalPages, response.total);
                $('#currentJobPage').val(response.currentPage);
            } else {
                alert('加载任务列表失败: ' + response.message);
            }
        },
        error: function() {
            alert('加载任务列表失败');
        }
    });
}

// 渲染任务分页
function renderJobPagination(currentPage, totalPages, total) {
    var pagination = $('#jobsPagination');
    pagination.empty();
    
    if (totalPages <= 1) {
        return;
    }
    
    var html = '<nav aria-label="任务分页"><ul class="pagination justify-content-center">';
    
    // 上一页
    if (currentPage > 0) {
        html += '<li class="page-item"><a class="page-link" href="javascript:loadJobs(' + (currentPage - 1) + ')">上一页</a></li>';
    } else {
        html += '<li class="page-item disabled"><a class="page-link" href="#">上一页</a></li>';
    }
    
    // 页码
    var start = Math.max(0, currentPage - 2);
    var end = Math.min(totalPages, start + 5);
    
    for (var i = start; i < end; i++) {
        if (i === currentPage) {
            html += '<li class="page-item active"><a class="page-link" href="#">' + (i + 1) + '</a></li>';
        } else {
            html += '<li class="page-item"><a class="page-link" href="javascript:loadJobs(' + i + ')">' + (i + 1) + '</a></li>';
        }
    }
    
    // 下一页
    if (currentPage < totalPages - 1) {
        html += '<li class="page-item"><a class="page-link" href="javascript:loadJobs(' + (currentPage + 1) + ')">下一页</a></li>';
    } else {
        html += '<li class="page-item disabled"><a class="page-link" href="#">下一页</a></li>';
    }
    
    html += '</ul></nav>';
    html += '<div class="text-center text-muted">共 ' + total + ' 条记录，第 ' + (currentPage + 1) + ' 页，共 ' + totalPages + ' 页</div>';
    
    pagination.html(html);
}

// 加载执行日志
function loadLogs(page) {
    $.ajax({
        url: '/api/job-logs?page=' + page + '&size=10&sort=createdAt,desc',
        type: 'GET',
        success: function(response) {
            if (response.success) {
                var tbody = $('#logsTable tbody');
                tbody.empty();
                
                $.each(response.data, function(index, log) {
                    var statusText = log.status === 1 ? 
                        '<span class="badge bg-success">成功</span>' : 
                        '<span class="badge bg-danger">失败</span>';
                    
                    var row = '<tr>' +
                        '<td>' + (log.id || '') + '</td>' +
                        '<td>' + (log.jobName || '') + '</td>' +
                        '<td>' + (log.jobGroup || '') + '</td>' +
                        '<td>' + statusText + '</td>' +
                        '<td>' + (log.startTime || '') + '</td>' +
                        '<td>' + (log.endTime || '') + '</td>' +
                        '<td>' + (log.duration || 0) + '</td>' +
                    '</tr>';
                    tbody.append(row);
                });
                
                // 更新分页信息
                renderLogPagination(response.currentPage, response.totalPages, response.total);
                $('#currentLogPage').val(response.currentPage);
            } else {
                alert('加载执行日志失败: ' + response.message);
            }
        },
        error: function() {
            alert('加载执行日志失败');
        }
    });
}

// 渲染日志分页
function renderLogPagination(currentPage, totalPages, total) {
    var pagination = $('#logsPagination');
    pagination.empty();
    
    if (totalPages <= 1) {
        return;
    }
    
    var html = '<nav aria-label="日志分页"><ul class="pagination justify-content-center">';
    
    // 上一页
    if (currentPage > 0) {
        html += '<li class="page-item"><a class="page-link" href="javascript:loadLogs(' + (currentPage - 1) + ')">上一页</a></li>';
    } else {
        html += '<li class="page-item disabled"><a class="page-link" href="#">上一页</a></li>';
    }
    
    // 页码
    var start = Math.max(0, currentPage - 2);
    var end = Math.min(totalPages, start + 5);
    
    for (var i = start; i < end; i++) {
        if (i === currentPage) {
            html += '<li class="page-item active"><a class="page-link" href="#">' + (i + 1) + '</a></li>';
        } else {
            html += '<li class="page-item"><a class="page-link" href="javascript:loadLogs(' + i + ')">' + (i + 1) + '</a></li>';
        }
    }
    
    // 下一页
    if (currentPage < totalPages - 1) {
        html += '<li class="page-item"><a class="page-link" href="javascript:loadLogs(' + (currentPage + 1) + ')">下一页</a></li>';
    } else {
        html += '<li class="page-item disabled"><a class="page-link" href="#">下一页</a></li>';
    }
    
    html += '</ul></nav>';
    html += '<div class="text-center text-muted">共 ' + total + ' 条记录，第 ' + (currentPage + 1) + ' 页，共 ' + totalPages + ' 页</div>';
    
    pagination.html(html);
}

// 添加任务按钮点击事件
function addJob() {
    // 清空表单
    $('#jobForm')[0].reset();
    $('#jobId').val('');
    $('#jobModalLabel').text('添加定时任务');
    $('#jobModal').modal('show');
}

// 编辑任务
function editJob(jobId) {
    $.ajax({
        url: '/api/jobs/' + jobId,
        type: 'GET',
        success: function(response) {
            if (response.success) {
                var job = response.data;
                $('#jobId').val(job.id);
                $('#jobName').val(job.jobName);
                $('#jobGroup').val(job.jobGroup);
                $('#cronExpression').val(job.cronExpression);
                $('#beanName').val(job.beanName);
                $('#methodName').val(job.methodName);
                $('#methodParams').val(job.methodParams);
                $('#remark').val(job.remark);
                $('#distributed').prop('checked', job.distributed); // 设置分布式执行选项
                
                $('#jobModalLabel').text('编辑定时任务');
                $('#jobModal').modal('show');
            } else {
                alert('获取任务信息失败: ' + response.message);
            }
        },
        error: function() {
            alert('获取任务信息失败');
        }
    });
}

// 保存任务
function saveJob() {
    var jobId = $('#jobId').val();
    var job = {
        jobName: $('#jobName').val(),
        jobGroup: $('#jobGroup').val(),
        cronExpression: $('#cronExpression').val(),
        beanName: $('#beanName').val(),
        methodName: $('#methodName').val(),
        methodParams: $('#methodParams').val(),
        remark: $('#remark').val(),
        distributed: $('#distributed').is(':checked') // 获取分布式执行选项
    };
    
    var url = '/api/jobs';
    var method = 'POST';
    
    if (jobId) {
        // 更新任务
        url = '/api/jobs/' + jobId;
        method = 'PUT';
    }
    
    $.ajax({
        url: url,
        type: method,
        contentType: 'application/json',
        data: JSON.stringify(job),
        success: function(response) {
            if (response.success) {
                alert('任务保存成功');
                $('#jobModal').modal('hide');
                var currentJobPage = $('#currentJobPage').val() || 0;
                loadJobs(currentJobPage);
                // 清空表单
                $('#jobForm')[0].reset();
                $('#jobId').val('');
                $('#jobModalLabel').text('添加定时任务');
            } else {
                alert('任务保存失败: ' + response.message);
            }
        },
        error: function() {
            alert('任务保存失败');
        }
    });
}

// 暂停任务
function pauseJob(jobId) {
    if (confirm('确定要暂停该任务吗？')) {
        $.ajax({
            url: '/api/jobs/' + jobId + '/pause',
            type: 'POST',
            success: function(response) {
                if (response.success) {
                    alert('任务已暂停');
                    var currentJobPage = $('#currentJobPage').val() || 0;
                    loadJobs(currentJobPage);
                } else {
                    alert('暂停任务失败: ' + response.message);
                }
            },
            error: function() {
                alert('暂停任务失败');
            }
        });
    }
}

// 恢复任务
function resumeJob(jobId) {
    if (confirm('确定要恢复该任务吗？')) {
        $.ajax({
            url: '/api/jobs/' + jobId + '/resume',
            type: 'POST',
            success: function(response) {
                if (response.success) {
                    alert('任务已恢复');
                    var currentJobPage = $('#currentJobPage').val() || 0;
                    loadJobs(currentJobPage);
                } else {
                    alert('恢复任务失败: ' + response.message);
                }
            },
            error: function() {
                alert('恢复任务失败');
            }
        });
    }
}

// 立即执行任务
function runJob(jobId) {
    if (confirm('确定要立即执行该任务吗？')) {
        $.ajax({
            url: '/api/jobs/' + jobId + '/run',
            type: 'POST',
            success: function(response) {
                if (response.success) {
                    alert('任务已启动');
                } else {
                    alert('执行任务失败: ' + response.message);
                }
            },
            error: function() {
                alert('执行任务失败');
            }
        });
    }
}

// 删除任务
function deleteJob(jobId) {
    if (confirm('确定要删除该任务吗？')) {
        $.ajax({
            url: '/api/jobs/' + jobId,
            type: 'DELETE',
            success: function(response) {
                if (response.success) {
                    alert('任务已删除');
                    var currentJobPage = $('#currentJobPage').val() || 0;
                    loadJobs(currentJobPage);
                } else {
                    alert('删除任务失败: ' + response.message);
                }
            },
            error: function() {
                alert('删除任务失败');
            }
        });
    }
}