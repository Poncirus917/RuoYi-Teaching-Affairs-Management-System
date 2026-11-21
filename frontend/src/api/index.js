import request from '@/utils/request'

// 获取首页统计数据
export function getIndexData() {
  return request({
    url: '/getIndexData',
    method: 'get'
  })
}