<template>
    <view class="heatmap-view">
        <view class="month-selector">
            <view class="selector-btn" @click="prevMonth">
                <text class="btn-icon">&lt;</text>
            </view>
            <view class="month-label">
                <text>{{ year }}年{{ month }}月</text>
            </view>
            <view class="selector-btn" @click="nextMonth">
                <text class="btn-icon">&gt;</text>
            </view>
        </view>

        <view class="calendar-header">
            <text class="weekday" v-for="day in weekdays" :key="day">{{ day }}</text>
        </view>

        <view class="calendar-grid">
            <view
                v-for="(day, index) in calendarDays"
                :key="index"
                class="day-cell"
                :class="{
                    'checked-in': day.checkedIn,
                    'today': day.isToday,
                    'empty': day.empty
                }"
                @click="showDetail(day)"
            >
                <text class="day-number">{{ day.number }}</text>
            </view>
        </view>

        <view class="heatmap-legend">
            <view class="legend-item">
                <view class="legend-color checked"></view>
                <text class="legend-text">已入睡</text>
            </view>
            <view class="legend-item">
                <view class="legend-color unchecked"></view>
                <text class="legend-text">未入睡</text>
            </view>
        </view>

        <view class="month-summary">
            <text>本月已睡觉 {{ totalDays }} 天</text>
        </view>

        <!-- 详情弹窗 -->
        <view class="detail-modal" v-if="selectedDay" @click="closeDetail">
            <view class="detail-content" @click.stop>
                <view class="detail-header">
                    <text class="detail-title">{{ selectedDay.dateStr }}</text>
                </view>
                <view class="detail-body">
                    <text v-if="selectedDay.checkedIn" class="detail-status success">已入睡</text>
                    <text v-else class="detail-status">未入睡</text>
                </view>
            </view>
        </view>
    </view>
</template>

<script setup>
import { ref, computed, watch } from 'vue'

const props = defineProps({
    year: {
        type: Number,
        default: new Date().getFullYear()
    },
    month: {
        type: Number,
        default: new Date().getMonth() + 1
    },
    days: {
        type: Object,
        default: () => ({})
    },
    totalDays: {
        type: Number,
        default: 0
    }
})

const emit = defineEmits(['monthChange'])

const selectedDay = ref(null)
const weekdays = ['一', '二', '三', '四', '五', '六', '日']

const calendarDays = computed(() => {
    const year = props.year
    const month = props.month

    // 获取当月第一天和最后一天
    const firstDay = new Date(year, month - 1, 1)
    const lastDay = new Date(year, month, 0)
    const daysInMonth = lastDay.getDate()

    // 获取第一天是星期几 (0-6, 0是周日)
    let firstDayOfWeek = firstDay.getDay()
    // 转换为周一为0
    firstDayOfWeek = firstDayOfWeek === 0 ? 6 : firstDayOfWeek - 1

    const today = new Date()

    const days = []

    // 添加空白天数
    for (let i = 0; i < firstDayOfWeek; i++) {
        days.push({ empty: true, number: '' })
    }

    // 添加实际天数
    for (let day = 1; day <= daysInMonth; day++) {
        const dateStr = `${year}-${String(month).padStart(2, '0')}-${String(day).padStart(2, '0')}`
        const isToday = today.getFullYear() === year &&
                       today.getMonth() + 1 === month &&
                       today.getDate() === day

        days.push({
            number: day,
            checkedIn: props.days[day] || false,
            dateStr: dateStr,
            isToday: isToday,
            empty: false
        })
    }

    return days
})

function prevMonth() {
    let newYear = props.year
    let newMonth = props.month - 1

    if (newMonth < 1) {
        newMonth = 12
        newYear--
    }

    emit('monthChange', { year: newYear, month: newMonth })
}

function nextMonth() {
    let newYear = props.year
    let newMonth = props.month + 1

    if (newMonth > 12) {
        newMonth = 1
        newYear++
    }

    const today = new Date()
    const maxYear = today.getFullYear()
    const maxMonth = today.getMonth() + 1

    // 不能超过当前月份
    if (newYear > maxYear || (newYear === maxYear && newMonth > maxMonth)) {
        return
    }

    emit('monthChange', { year: newYear, month: newMonth })
}

function showDetail(day) {
    if (!day.empty) {
        selectedDay.value = day
    }
}

function closeDetail() {
    selectedDay.value = null
}
</script>

<style scoped>
.heatmap-view {
    background: #fff;
    border-radius: 20rpx;
    padding: 30rpx;
    box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.1);
}

.month-selector {
    display: flex;
    align-items: center;
    justify-content: center;
    margin-bottom: 30rpx;
}

.selector-btn {
    padding: 10rpx 30rpx;
    background: #f5f5f5;
    border-radius: 10rpx;
}

.btn-icon {
    font-size: 36rpx;
    color: #667eea;
    font-weight: bold;
}

.month-label {
    flex: 1;
    text-align: center;
    font-size: 32rpx;
    font-weight: bold;
    color: #333;
}

.calendar-header {
    display: grid;
    grid-template-columns: repeat(7, 1fr);
    text-align: center;
    margin-bottom: 20rpx;
}

.weekday {
    font-size: 24rpx;
    color: #999;
    padding: 10rpx 0;
}

.calendar-grid {
    display: grid;
    grid-template-columns: repeat(7, 1fr);
    gap: 10rpx;
}

.day-cell {
    aspect-ratio: 1;
    display: flex;
    align-items: center;
    justify-content: center;
    border-radius: 8rpx;
    background: #f5f5f5;
    min-height: 60rpx;
    position: relative;
}

.day-cell.empty {
    background: transparent;
}

.day-cell.checked-in {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.day-cell.checked-in .day-number {
    color: #fff;
}

.day-cell.today {
    border: 2rpx solid #667eea;
}

.day-number {
    font-size: 24rpx;
    color: #666;
}

.heatmap-legend {
    display: flex;
    justify-content: center;
    gap: 40rpx;
    margin-top: 30rpx;
    padding-top: 20rpx;
    border-top: 1rpx solid #eee;
}

.legend-item {
    display: flex;
    align-items: center;
    gap: 10rpx;
}

.legend-color {
    width: 30rpx;
    height: 30rpx;
    border-radius: 6rpx;
}

.legend-color.checked {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.legend-color.unchecked {
    background: #f5f5f5;
}

.legend-text {
    font-size: 22rpx;
    color: #999;
}

.month-summary {
    text-align: center;
    margin-top: 20rpx;
    font-size: 26rpx;
    color: #667eea;
    font-weight: bold;
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
    max-width: 400rpx;
}

.detail-header {
    display: flex;
    flex-direction: column;
    align-items: center;
    margin-bottom: 20rpx;
    padding-bottom: 20rpx;
    border-bottom: 1rpx solid #eee;
}

.detail-title {
    font-size: 32rpx;
    font-weight: bold;
    color: #333;
}

.detail-body {
    display: flex;
    flex-direction: column;
    align-items: center;
}

.detail-status {
    font-size: 28rpx;
    color: #999;
}

.detail-status.success {
    color: #667eea;
    font-weight: bold;
}
</style>
