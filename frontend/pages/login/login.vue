<template>
    <view class="login-container">
        <view class="logo-section">
            <text class="logo">😴</text>
            <text class="app-name">睡了么</text>
            <text class="app-desc">每晚提醒，健康睡眠</text>
        </view>

        <view class="form-card">
            <view class="tab-bar">
                <text
                    class="tab-item"
                    :class="{ active: currentTab === 'login' }"
                    @click="switchTab('login')"
                >
                    登录
                </text>
                <text
                    class="tab-item"
                    :class="{ active: currentTab === 'register' }"
                    @click="switchTab('register')"
                >
                    注册
                </text>
            </view>

            <view class="form-content">
                <view class="input-group" v-if="currentTab === 'register'">
                    <text class="label">用户名</text>
                    <input
                        class="input"
                        v-model="form.username"
                        placeholder="请输入用户名"
                    />
                </view>

                <view class="input-group">
                    <text class="label">手机号</text>
                    <input
                        class="input"
                        v-model="form.phone"
                        placeholder="请输入手机号"
                        type="number"
                    />
                </view>

                <view class="input-group">
                    <text class="label">密码</text>
                    <input
                        class="input"
                        v-model="form.password"
                        placeholder="请输入密码"
                        password
                    />
                </view>

                <button class="submit-btn" @click="handleSubmit">
                    {{ currentTab === 'login' ? '登录' : '注册' }}
                </button>
            </view>
        </view>
    </view>
</template>

<script setup>
import { ref } from 'vue'
import api from '@/api/index.js'

const currentTab = ref('login')
const form = ref({
    username: '',
    phone: '',
    password: ''
})

function switchTab(tab) {
    currentTab.value = tab
    form.value = {
        username: '',
        phone: '',
        password: ''
    }
}

async function handleSubmit() {
    if (!form.value.phone || !form.value.password) {
        uni.showToast({
            title: '请填写完整信息',
            icon: 'none'
        })
        return
    }

    if (currentTab.value === 'register' && !form.value.username) {
        uni.showToast({
            title: '请输入用户名',
            icon: 'none'
        })
        return
    }

    try {
        uni.showLoading({
            title: currentTab.value === 'login' ? '登录中...' : '注册中...'
        })

        let res
        if (currentTab.value === 'login') {
            res = await api.login({
                phone: form.value.phone,
                password: form.value.password
            })
        } else {
            res = await api.register({
                username: form.value.username,
                phone: form.value.phone,
                password: form.value.password
            })
        }

        uni.hideLoading()

        if (res.code === 200) {
            uni.setStorageSync('user', res.data.user)
            uni.setStorageSync('token', res.data.token)

            uni.showToast({
                title: currentTab.value === 'login' ? '登录成功' : '注册成功',
                icon: 'success'
            })

            setTimeout(() => {
                uni.switchTab({
                    url: '/pages/index/index'
                })
            }, 1500)
        } else {
            uni.showToast({
                title: res.message || '操作失败',
                icon: 'none'
            })
        }
    } catch (e) {
        uni.hideLoading()
        console.error('请求失败:', e)
        uni.showToast({
            title: '网络错误: ' + JSON.stringify(e),
            icon: 'none',
            duration: 3000
        })
    }
}
</script>

<style scoped>
.login-container {
    min-height: 100vh;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    padding: 100rpx 40rpx;
}

.logo-section {
    text-align: center;
    margin-bottom: 80rpx;
}

.logo {
    display: block;
    font-size: 120rpx;
    margin-bottom: 20rpx;
}

.app-name {
    display: block;
    font-size: 48rpx;
    font-weight: bold;
    color: #fff;
    margin-bottom: 10rpx;
}

.app-desc {
    display: block;
    font-size: 28rpx;
    color: rgba(255, 255, 255, 0.8);
}

.form-card {
    background: #fff;
    border-radius: 24rpx;
    padding: 40rpx;
    box-shadow: 0 10rpx 40rpx rgba(0, 0, 0, 0.1);
}

.tab-bar {
    display: flex;
    border-bottom: 1rpx solid #eee;
    margin-bottom: 40rpx;
}

.tab-item {
    flex: 1;
    text-align: center;
    padding: 20rpx 0;
    font-size: 32rpx;
    color: #999;
    position: relative;
}

.tab-item.active {
    color: #667eea;
    font-weight: bold;
}

.tab-item.active::after {
    content: '';
    position: absolute;
    bottom: -1rpx;
    left: 50%;
    transform: translateX(-50%);
    width: 60rpx;
    height: 4rpx;
    background: #667eea;
    border-radius: 2rpx;
}

.input-group {
    margin-bottom: 30rpx;
}

.label {
    display: block;
    font-size: 28rpx;
    color: #666;
    margin-bottom: 15rpx;
}

.input {
    background: #f5f5f5;
    border-radius: 12rpx;
    padding: 24rpx;
    font-size: 28rpx;
}

.submit-btn {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: #fff;
    border-radius: 50rpx;
    margin-top: 20rpx;
}
</style>
