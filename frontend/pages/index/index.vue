<template>
    <view class="container">
        <view class="card">
            <view class="header">
                <text class="greeting">晚上好</text>
                <text class="username">{{ username }}</text>
            </view>

            <view class="sleep-info">
                <view class="info-row">
                    <text class="label">目标睡觉时间</text>
                    <text class="value">{{ deadlineTime || '未设置' }}</text>
                </view>
                <view class="info-row">
                    <text class="label">当前时间</text>
                    <text class="value">{{ currentTime }}</text>
                </view>
            </view>
        </view>

        <view class="card checkin-card">
            <view class="checkin-status" v-if="hasCheckedIn">
                <text class="status-icon">✓</text>
                <text class="status-text">今晚已睡觉打卡</text>
                <text class="checkin-time">{{ checkInTime }}</text>
            </view>

            <button
                v-else
                class="checkin-btn"
                :class="{ disabled: !canCheckIn }"
                :disabled="!canCheckIn"
                @click="handleCheckIn"
            >
                <text class="btn-text">{{ checkInBtnText }}</text>
            </button>
        </view>

        <view class="card">
            <view class="section-title">睡觉统计</view>
            <view class="stats">
                <view class="stat-item">
                    <text class="stat-value">{{ stats.totalDays }}</text>
                    <text class="stat-label">累计睡觉</text>
                </view>
                <view class="stat-item">
                    <text class="stat-value">{{ stats.currentStreak }}</text>
                    <text class="stat-label">连续睡觉</text>
                </view>
            </view>
        </view>

        <view class="card info-card">
            <view class="info-title">💡 提示</view>
            <text class="info-text">每晚 {{ deadlineTime || '23:00' }} 前记得打卡睡觉，否则会通知紧急联系人哦~</text>
        </view>
    </view>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import api from '@/api/index.js'

const username = ref('')
const deadlineTime = ref('')
const currentTime = ref('')
const hasCheckedIn = ref(false)
const checkInTime = ref('')
const canCheckIn = ref(true)
const stats = ref({
    totalDays: 0,
    currentStreak: 0
})

const checkInBtnText = computed(() => {
    if (!canCheckIn.value) {
        return '请先设置睡觉时间'
    }
    return '我要睡觉了'
})

let timer = null

function updateTime() {
    const now = new Date()
    const hours = String(now.getHours()).padStart(2, '0')
    const minutes = String(now.getMinutes()).padStart(2, '0')
    currentTime.value = `${hours}:${minutes}`
}

async function loadData() {
    const user = uni.getStorageSync('user')
    if (!user || !user.id) {
        uni.navigateTo({ url: '/pages/login/login' })
        return
    }

    username.value = user.username

    try {
        const settingRes = await api.getSleepSetting(user.id)
        if (settingRes.code === 200 && settingRes.data) {
            deadlineTime.value = settingRes.data.deadlineTime
        }

        const statusRes = await api.getCheckInStatus(user.id)
        if (statusRes.code === 200) {
            hasCheckedIn.value = statusRes.data
            if (hasCheckedIn.value) {
                checkInTime.value = currentTime.value
            }
        }
    } catch (e) {
        console.error('加载数据失败', e)
    }
}

async function handleCheckIn() {
    const user = uni.getStorageSync('user')
    if (!user || !user.id) {
        uni.navigateTo({ url: '/pages/login/login' })
        return
    }

    try {
        uni.showLoading({ title: '打卡中...' })

        const res = await api.checkIn({
            userId: user.id
        })

        uni.hideLoading()

        if (res.code === 200) {
            hasCheckedIn.value = true
            checkInTime.value = res.data.checkInTime
            uni.showToast({
                title: '晚安，好梦！',
                icon: 'success'
            })
        } else {
            uni.showToast({
                title: res.message || '打卡失败',
                icon: 'none'
            })
        }
    } catch (e) {
        uni.hideLoading()
        console.error('打卡失败', e)
        uni.showToast({
            title: '网络错误',
            icon: 'none'
        })
    }
}

onMounted(() => {
    loadData()
    updateTime()
    timer = setInterval(updateTime, 1000)
})

onUnmounted(() => {
    if (timer) {
        clearInterval(timer)
    }
})
</script>

<style scoped>
.header {
    text-align: center;
    margin-bottom: 40rpx;
}

.greeting {
    display: block;
    font-size: 28rpx;
    color: #999;
    margin-bottom: 10rpx;
}

.username {
    display: block;
    font-size: 48rpx;
    font-weight: bold;
    color: #333;
}

.sleep-info {
    border-top: 1rpx solid #eee;
    padding-top: 30rpx;
}

.info-row {
    display: flex;
    justify-content: space-between;
    margin-bottom: 20rpx;
}

.info-row:last-child {
    margin-bottom: 0;
}

.label {
    font-size: 28rpx;
    color: #666;
}

.value {
    font-size: 28rpx;
    font-weight: bold;
    color: #333;
}

.checkin-card {
    text-align: center;
    padding: 60rpx 30rpx;
}

.checkin-status {
    display: flex;
    flex-direction: column;
    align-items: center;
}

.status-icon {
    font-size: 100rpx;
    color: #52c41a;
    margin-bottom: 20rpx;
}

.status-text {
    font-size: 32rpx;
    color: #52c41a;
    font-weight: bold;
    margin-bottom: 10rpx;
}

.checkin-time {
    font-size: 24rpx;
    color: #999;
}

.checkin-btn {
    width: 400rpx;
    height: 400rpx;
    border-radius: 50%;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    border: none;
    color: #fff;
    font-size: 36rpx;
    font-weight: bold;
    display: flex;
    align-items: center;
    justify-content: center;
    box-shadow: 0 10rpx 40rpx rgba(102, 126, 234, 0.4);
}

.checkin-btn:active {
    opacity: 0.8;
}

.checkin-btn.disabled {
    background: #ccc;
    box-shadow: none;
}

.section-title {
    font-size: 32rpx;
    font-weight: bold;
    color: #333;
    margin-bottom: 30rpx;
}

.stats {
    display: flex;
    justify-content: space-around;
}

.stat-item {
    display: flex;
    flex-direction: column;
    align-items: center;
}

.stat-value {
    font-size: 48rpx;
    font-weight: bold;
    color: #667eea;
    margin-bottom: 10rpx;
}

.stat-label {
    font-size: 24rpx;
    color: #999;
}

.info-card {
    background: linear-gradient(135deg, #ffeaa7 0%, #fdcb6e 100%);
}

.info-title {
    font-size: 28rpx;
    font-weight: bold;
    margin-bottom: 15rpx;
    color: #2d3436;
}

.info-text {
    font-size: 24rpx;
    color: #2d3436;
    line-height: 1.6;
}
</style>
