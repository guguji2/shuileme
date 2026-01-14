<template>
    <view class="stats-page">
        <view class="container">
            <!-- 概览卡片 -->
            <OverviewCard
                :total-days="overviewData.totalDays"
                :current-streak="overviewData.currentStreak"
                :monthly-days="overviewData.monthlyDays"
            />

            <!-- Tab 切换 -->
            <TabBar :active-tab="activeTab" @change="handleTabChange" />

            <!-- 内容区域 -->
            <view class="content-area">
                <!-- 周视图 -->
                <WeeklyView
                    v-if="activeTab === 'weekly'"
                    :dates="weeklyData.dates"
                    :counts="weeklyData.counts"
                    :check-in-times="weeklyData.checkInTimes"
                />

                <!-- 月度热力图 -->
                <MonthlyHeatmapView
                    v-else
                    :year="heatmapData.year"
                    :month="heatmapData.month"
                    :days="heatmapData.days"
                    :total-days="heatmapData.totalDays"
                    @month-change="handleMonthChange"
                />
            </view>
        </view>
    </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { onPullDownRefresh } from '@dcloudio/uni-app'
import api from '@/api/index.js'
import OverviewCard from '@/components/OverviewCard.vue'
import TabBar from '@/components/TabBar.vue'
import WeeklyView from '@/components/WeeklyView.vue'
import MonthlyHeatmapView from '@/components/MonthlyHeatmapView.vue'

const activeTab = ref('weekly')
const overviewData = ref({
    totalDays: 0,
    currentStreak: 0,
    monthlyDays: 0,
    longestStreak: 0
})
const weeklyData = ref({
    dates: [],
    counts: [],
    checkInTimes: []
})
const heatmapData = ref({
    year: new Date().getFullYear(),
    month: new Date().getMonth() + 1,
    days: {},
    totalDays: 0
})

async function loadOverviewData() {
    try {
        const user = uni.getStorageSync('user')
        if (!user || !user.id) {
            return
        }

        const res = await api.getOverviewStats(user.id)
        if (res.code === 200 && res.data) {
            overviewData.value = { ...overviewData.value, ...res.data }
        }
    } catch (error) {
        console.error('加载概览数据失败:', error)
        uni.showToast({
            title: '加载失败',
            icon: 'none'
        })
    }
}

async function loadWeeklyData() {
    try {
        const user = uni.getStorageSync('user')
        if (!user || !user.id) {
            return
        }

        const res = await api.getWeeklyStats(user.id)
        if (res.code === 200 && res.data) {
            weeklyData.value = { ...weeklyData.value, ...res.data }
        }
    } catch (error) {
        console.error('加载周数据失败:', error)
        uni.showToast({
            title: '加载失败',
            icon: 'none'
        })
    }
}

async function loadMonthlyData(year, month) {
    try {
        const user = uni.getStorageSync('user')
        if (!user || !user.id) {
            return
        }

        const res = await api.getMonthlyStats(user.id, year, month)
        if (res.code === 200 && res.data) {
            heatmapData.value = { ...heatmapData.value, ...res.data }
        }
    } catch (error) {
        console.error('加载月度数据失败:', error)
        uni.showToast({
            title: '加载失败',
            icon: 'none'
        })
    }
}

function handleTabChange(tab) {
    activeTab.value = tab

    // 如果切换到月度热力图且数据为空，加载数据
    if (tab === 'monthly' && Object.keys(heatmapData.value.days).length === 0) {
        loadMonthlyData(heatmapData.value.year, heatmapData.value.month)
    }
}

function handleMonthChange({ year, month }) {
    loadMonthlyData(year, month)
}

// 下拉刷新
onPullDownRefresh(() => {
    Promise.all([
        loadOverviewData(),
        loadWeeklyData(),
        loadMonthlyData(heatmapData.value.year, heatmapData.value.month)
    ]).finally(() => {
        uni.stopPullDownRefresh()
    })
})

onMounted(() => {
    loadOverviewData()
    loadWeeklyData()
    loadMonthlyData(heatmapData.value.year, heatmapData.value.month)
})
</script>

<style scoped>
.stats-page {
    min-height: 100vh;
    background: #f5f5f5;
}

.container {
    padding: 20rpx;
}

.content-area {
    min-height: 400rpx;
}
</style>
