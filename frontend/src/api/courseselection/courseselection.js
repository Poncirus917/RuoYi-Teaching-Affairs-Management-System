import request from '@/utils/request'

// 这里的路径前缀必须与 Controller 的 @RequestMapping 完全一致
const prefix = '/courseselection/courseselection'

// 查询选课记录列表
export function listCourseselection(query) {
  return request({
    url: prefix + '/list',
    method: 'get',
    params: query
  })
}

// 查询选课记录详细
export function getCourseselection(studentId, classId) {
  // 增加简单防御，防止 404
  if (!studentId || !classId) {
    console.error("获取详情失败：学号或班级号为空", studentId, classId);
  }
  return request({
    url: prefix + '/' + studentId + '/' + classId,
    method: 'get'
  })
}

// 新增选课记录
export function addCourseselection(data) {
  return request({
    url: prefix,
    method: 'post',
    data: data
  })
}

// 修改选课记录
export function updateCourseselection(data) {
  return request({
    url: prefix,
    method: 'put',
    data: data
  })
}

// 删除选课记录
export function delCourseselection(studentId, classId) {
  return request({
    url: prefix + '/' + studentId + '/' + classId,
    method: 'delete'
  })
}