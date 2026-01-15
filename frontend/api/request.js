// API 基础配置
const BASE_URL = 'http://101.33.210.146:8080/api'

// 封装请求方法
function request(url, method = 'GET', data = {}) {
    return new Promise((resolve, reject) => {
        uni.request({
            url: BASE_URL + url,
            method: method,
            data: data,
            header: {
                'Content-Type': 'application/json'
            },
            success: (res) => {
                if (res.statusCode === 200) {
                    resolve(res.data)
                } else {
                    reject(res.data)
                }
            },
            fail: (err) => {
                reject(err)
            }
        })
    })
}

export default {
    get(url, data) {
        return request(url, 'GET', data)
    },
    post(url, data) {
        return request(url, 'POST', data)
    },
    put(url, data) {
        return request(url, 'PUT', data)
    },
    delete(url, data) {
        return request(url, 'DELETE', data)
    }
}
