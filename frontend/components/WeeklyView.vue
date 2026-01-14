<template>
    <view class="weekly-view">
        <view class="chart-container">
            <view
                v-for="(day, index) in displayData"
                :key="index"
                class="bar-item"
                @click="showDetail(day, index)"
            >
                <view class="bar-wrapper">
                    <view
                        class="bar"
                        :class="{ active: day.count === 1, today: day.isToday }"
                        :style="{ height: day.count === 1 ? '100%' : '10%' }"
                    ></view>
                </view>
                <text class="date-label">{{ day.date }}</text>
                <text class="weekday-label">{{ day.weekday }}</text>
            </view>
        </view>

        <!-- 详情弹窗 -->
        <view class="detail-modal" v-if="selectedDay" @click="closeDetail">
            <view class="detail-content" @click.stop>
                <view class="detail-header">
                    <text class="detail-title">{{ selectedDay.date }}</text>
                    <text class="detail-subtitle">{{ selectedDay.weekday }}</text>
                </view>
                <view class="detail-body">
                    <text v-if="selectedDay.count === 1" class="detail-time">
                        睡觉时间: {{ selectedDay.checkInTime }}
                    </text>
                    <text v-else class="detail-empty">未入睡</text>
                </view>
            </view>
        </view>
    </view>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'

const props = defineProps({
    dates: {
        type: Array,
        default: () => []
    },
    counts: {
        type: Array,
        default: () => []
    },
    checkInTimes: {
        type: Array,
        default: () => []
    }
})

const selectedDay = ref(null)
const weekdays = ['周日', '周一', '周二', '周三', '周四', '周五', '周六']

const displayData = computed(() => {
    const today = new Date()
    const result = []

    for (let i = 0; i < props.dates.length; i++) {
        const dateStr = props.dates[i]
        const [month, day] = dateStr.split('-').map(Number)
        const date = new Date()
        date.setMonth(month - 1)
        date.setDate(day)

        result.push({
            date: dateStr,
            weekday: weekdays[date.getDay()],
            count: props.counts[i] || 0,
            checkInTime: props.checkInTimes[i] || null,
            isToday: isSameDay(date, today)
        })
    }

    return result
})

function isSameDay(date1, date2) {
    return date1.getDate() === date2.getDate() &&
           date1.getMonth() === date2.getMonth() &&
           date1.getFullYear() === date2.getFullYear()
}

function showDetail(day, index) {
    selectedDay.value = day
}

function closeDetail() {
    selectedDay.value = null
}
</script>

<style scoped>
.weekly-view {
    background: #fff;
    border-radius: 20rpx;
    padding: 30rpx;
    box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.1);
}

.chart-container {
    display: flex;
    justify-content: space-around;
    align-items: flex-end;
    height: 300rpx;
    padding-top: 20rpx;
}

.bar-item {
    display: flex;
    flex-direction: column;
    align-items: center;
    flex: 1;
}

.bar-wrapper {
    height: 220rpx;
    width: 60rpx;
    display: flex;
    align-items: flex-end;
    margin-bottom: 10rpx;
}

.bar {
    width: 100%;
    background: #e0e0e0;
    border-radius: 8rpx 8rpx 0 0;
    transition: height 0.3s ease;
    min-height: 20rpx;
}

.bar.active {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.bar.today {
    box-shadow: 0 0 10rpx rgba(102, 126, 234, 0.5);
}

.date-label {
    font-size: 22rpx;
    color: #666;
    margin-bottom: 4rpx;
}

.weekday-label {
    font-size: 20rpx;
    color: #999;
}

.detail-modal {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: rgba(0, 0, 0, 0.5);
    display: flex;
    align-items: center;
    justify-content: center;
    z-index: 999;
}

.detail-content {
    background: #fff;
    border-radius: 20rpx;
    padding: 40rpx;
    width: 80%;
    max-width: 500rpx;
}

.detail-header {
    display: flex;
    flex-direction: column;
    align-items: center;
    margin-bottom: 30rpx;
    padding-bottom: 20rpx;
    border-bottom: 1rpx solid #eee;
}

.detail-title {
    font-size: 36rpx;
    font-weight: bold;
    color: #333;
    margin-bottom: 10rpx;
}

.detail-subtitle {
    font-size: 24rpx;
    color: #999;
}

.detail-body {
    display: flex;
    flex-direction: column;
    align-items: center;
}

.detail-time {
    font-size: 32rpx;
    color: #667eea;
    font-weight: bold;
}

.detail-empty {
    font-size: 28rpx;
    color: #999;
}
</style>
