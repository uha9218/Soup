<!-- The exported code uses Tailwind CSS. Install Tailwind CSS in your dev environment to ensure all styles work. -->

<template>
  <div class="min-h-screen bg-gray-50 py-8">
    <div class="mx-auto max-w-7xl px-4">
      <!-- Next Schedule Banner -->
      <div
        role="status"
        aria-live="polite"
        @click="goToSchedule(nextSchedule.id)"
        class="mb-8 bg-blue-50 p-4 rounded-lg cursor-pointer hover:bg-blue-100 transition-colors"
      >
        <p class="text-blue-800 text-lg">{{ nextSchedule.message }}</p>
      </div>

      <!-- Calendar Area -->
      <div class="grid grid-cols-10 gap-8 mb-8">
        <div class="col-span-7 bg-white rounded-lg shadow-sm p-6">
          <!-- Month Navigation -->
          <div class="flex items-center justify-between mb-6">
            <button @click="prevMonth" class="p-2 hover:bg-gray-100 rounded-full !rounded-button">
              <i class="fas fa-chevron-left"></i>
            </button>
            <h2 class="text-xl font-medium">{{ currentYear }}년 {{ currentMonth }}월</h2>
            <button @click="nextMonth" class="p-2 hover:bg-gray-100 rounded-full !rounded-button">
              <i class="fas fa-chevron-right"></i>
            </button>
          </div>

          <!-- Calendar Grid -->
          <div class="grid grid-cols-7 gap-2">
            <div v-for="day in ['일', '월', '화', '수', '목', '금', '토']" :key="day"
              class="text-center font-medium text-gray-600 p-2">
              {{ day }}
            </div>
            <div v-for="date in calendarDates" :key="date.date"
              :class="[
                'p-3 text-center rounded-lg cursor-pointer hover:bg-gray-50',
                date.isSelected ? 'bg-blue-50 border border-blue-200' : '',
                date.isToday ? 'font-bold text-blue-600' : '',
                date.isCurrentMonth ? 'text-gray-900' : 'text-gray-400'
              ]"
              @click="selectDate(date.date)"
            >
              <div class="mb-1">{{ date.dayOfMonth }}</div>
              <div v-if="date.scheduleCount" class="text-xs text-blue-600">
                {{ date.scheduleCount }}개
              </div>
            </div>
          </div>
        </div>

        <div class="col-span-3 bg-white rounded-lg shadow-sm p-6">
          <div class="flex items-center justify-between mb-4">
            <h3 class="text-lg font-medium">{{ scheduleListTitle }}</h3>
            <button v-if="selectedDate" 
              @click="clearDateSelection"
              class="text-sm text-blue-600 hover:text-blue-800 !rounded-button whitespace-nowrap">
              전체보기
            </button>
          </div>
          <div class="space-y-3">
            <div v-for="schedule in filteredSchedules" :key="schedule.id"
              @click="goToSchedule(schedule.id)"
              class="p-3 bg-gray-50 rounded-lg cursor-pointer hover:bg-gray-100"
            >
              <div class="text-sm text-gray-600">{{ formatDate(schedule.date) }}</div>
              <div class="font-medium">{{ schedule.title }}</div>
              <div class="text-sm text-gray-600">{{ schedule.time }}</div>
            </div>
          </div>
        </div>
      </div>

      <!-- Progress Area -->
      <div class="grid grid-cols-1 md:grid-cols-3 gap-8">
        <!-- Study Progress Overview -->
        <div class="bg-white rounded-lg shadow-sm p-6">
          <h3 class="text-lg font-medium mb-4">학습 진행 현황</h3>
          
          <!-- 로딩 상태 -->
          <div v-if="studyProgressLoading" class="text-center py-8">
            <div class="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600 mx-auto"></div>
            <p class="text-gray-500 mt-2">학습 진행 현황을 불러오는 중...</p>
          </div>
          
          <!-- 에러 상태 -->
          <div v-else-if="studyProgressError" class="text-center py-8">
            <p class="text-red-500 mb-2">{{ studyProgressError }}</p>
            <button @click="loadStudyProgress" class="text-blue-600 hover:text-blue-800">
              다시 시도
            </button>
          </div>
          
          <!-- 데이터 표시 -->
          <div v-else class="space-y-3">
            <div v-for="section in sections" :key="section.id"
              @click="selectSection(section)"
              :class="[
                'p-3 rounded-lg cursor-pointer',
                selectedSection?.id === section.id ? 'bg-blue-50 border border-blue-200' : 'hover:bg-gray-50'
              ]"
            >
              <div class="flex items-center justify-between">
                <span>섹션 {{ section.title }}</span>
                <i :class="[
                  'fas',
                  section.isCompleted ? 'fa-check text-green-500' : 'fa-clock text-gray-400'
                ]"></i>
              </div>
            </div>
            
            <!-- 데이터가 없는 경우 -->
            <div v-if="sections.length === 0" class="text-center py-8 text-gray-500">
              학습 진행 현황이 없습니다.
            </div>
          </div>
        </div>

        <!-- Member Progress Table -->
        <div class="md:col-span-2 bg-white rounded-lg shadow-sm p-6">
          <h3 class="text-lg font-medium mb-4">
            팀원 진행 상황 - {{ selectedSection ? `섹션 ${selectedSection.title}` : '' }}
          </h3>
          
          <!-- 로딩 상태 -->
          <div v-if="studyProgressLoading" class="text-center py-8">
            <div class="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600 mx-auto"></div>
            <p class="text-gray-500 mt-2">팀원 진행 상황을 불러오는 중...</p>
          </div>
          
          <!-- 에러 상태 -->
          <div v-else-if="studyProgressError" class="text-center py-8">
            <p class="text-red-500 mb-2">{{ studyProgressError }}</p>
            <button @click="loadStudyProgress" class="text-blue-600 hover:text-blue-800">
              다시 시도
            </button>
          </div>
          
          <!-- 데이터 표시 -->
          <div v-else-if="memberProgress.length" class="overflow-x-auto">
            <table class="w-full">
              <thead>
                <tr class="border-b">
                  <th class="py-3 text-left">팀원</th>
                  <th v-for="col in progressColumns" :key="col" class="py-3 text-center">
                    {{ col }}
                  </th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="member in memberProgress" :key="member.id" class="border-b">
                  <td class="py-3">{{ member.name }}</td>
                  <td v-for="(status, idx) in member.progress" :key="idx"
                    class="py-3 text-center"
                  >
                    <span :class="status === 'o' ? 'text-green-500' : 'text-red-500'">
                      {{ status }}
                    </span>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
          <div v-else class="text-center py-8 text-gray-500">
            선택된 섹션의 진행 정보가 없습니다.
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { ref, computed, onMounted } from 'vue';
import { scheduleApi } from '../services/scheduleApi.js';
import { studyProgressApi } from '../services/studyProgressApi.js';

// Type definitions
interface CalendarDate {
  date: string;
  dayOfMonth: number;
  isCurrentMonth: boolean;
  isSelected: boolean;
  isToday: boolean;
  scheduleCount: number;
}

interface Schedule {
  id: string;
  date: string;
  title: string;
  time: string;
}

interface Section {
  id: number;
  title: string;
  isCompleted: boolean;
}

interface MemberProgress {
  id: number;
  name: string;
  progress: string[];
}

// API 응답 타입 정의
interface StudyProgressResponse {
  studyName: string;
  schedules: ScheduleProgress[];
}

interface ScheduleProgress {
  scheduleId: number;
  scheduleName: string;
  scheduleDate: string;
  scheduleTime: string;
  description: string;
  meetingLocation: string;
  hasDeepStudy: boolean;
  sections: SectionProgress[];
}

interface SectionProgress {
  sectionId: number;
  sectionName: string;
  sectionNumber: number;
  needsReview: boolean;
  members: MemberProgressDetail[];
}

interface MemberProgressDetail {
  userId: number;
  userName: string;
  reviewSubmitted: boolean;
  deepStudySubmitted: boolean;
}

const nextSchedule = ref({
  id: 'sch-2025-08-16-2000',
  message: '3일 후 스프링 MVC 1편 온라인 미팅 진행 예정입니다'
});

const currentDate = ref(new Date());
const selectedDate = ref<string | null>(null);

const monthSchedules = ref<Schedule[]>([]);
const isLoading = ref(false);
const error = ref<string | null>(null);

const currentYear = computed(() => currentDate.value.getFullYear());
const currentMonth = computed(() => currentDate.value.getMonth() + 1);

const calendarDates = computed((): CalendarDate[] => {
  const year = currentDate.value.getFullYear();
  const month = currentDate.value.getMonth();
  const firstDay = new Date(year, month, 1);
  const lastDay = new Date(year, month + 1, 0);
  const dates: CalendarDate[] = [];

  // Previous month dates
  const firstDayOfWeek = firstDay.getDay();
  for (let i = firstDayOfWeek - 1; i >= 0; i--) {
    const date = new Date(year, month, -i);
    dates.push({
      date: formatDateString(date),
      dayOfMonth: date.getDate(),
      isCurrentMonth: false,
      isSelected: selectedDate.value === formatDateString(date),
      isToday: isSameDate(date, new Date()),
      scheduleCount: getScheduleCount(formatDateString(date))
    });
  }

  // Current month dates
  for (let i = 1; i <= lastDay.getDate(); i++) {
    const date = new Date(year, month, i);
    dates.push({
      date: formatDateString(date),
      dayOfMonth: i,
      isCurrentMonth: true,
      isSelected: selectedDate.value === formatDateString(date),
      isToday: isSameDate(date, new Date()),
      scheduleCount: getScheduleCount(formatDateString(date))
    });
  }

  // Next month dates
  const remainingDays = 42 - dates.length;
  for (let i = 1; i <= remainingDays; i++) {
    const date = new Date(year, month + 1, i);
    dates.push({
      date: formatDateString(date),
      dayOfMonth: i,
      isCurrentMonth: false,
      isSelected: selectedDate.value === formatDateString(date),
      isToday: isSameDate(date, new Date()),
      scheduleCount: getScheduleCount(formatDateString(date))
    });
  }

  return dates;
});

// 학습 진행 현황 관련 상태
const studyProgress = ref<StudyProgressResponse | null>(null);
const studyProgressLoading = ref(false);
const studyProgressError = ref<string | null>(null);

const sections = ref<Section[]>([]);
const selectedSection = ref<Section | null>(null);
const progressColumns = ref<string[]>([]);
const memberProgress = ref<MemberProgress[]>([]);

// 현재 진행 중인 스터디 ID (자동으로 조회됨)
const currentStudyId = ref(null);

const scheduleListTitle = computed(() => {
  return selectedDate.value ? `${formatDate(selectedDate.value)} 일정` : '이번 달 전체 일정';
});

const filteredSchedules = computed(() => {
  if (selectedDate.value) {
    return monthSchedules.value.filter(schedule => schedule.date === selectedDate.value);
  }
  return monthSchedules.value;
});

function formatDateString(date: Date): string {
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');
  return `${year}-${month}-${day}`;
}

function isSameDate(date1: Date, date2: Date): boolean {
  return date1.getFullYear() === date2.getFullYear() &&
    date1.getMonth() === date2.getMonth() &&
    date1.getDate() === date2.getDate();
}

function getScheduleCount(date: string): number {
  return monthSchedules.value.filter(schedule => schedule.date === date).length;
}

function formatDate(dateString: string): string {
  const [year, month, day] = dateString.split('-');
  return `${month}월 ${day}일`;
}

function prevMonth() {
  currentDate.value = new Date(currentDate.value.getFullYear(), currentDate.value.getMonth() - 1);
}

function nextMonth() {
  currentDate.value = new Date(currentDate.value.getFullYear(), currentDate.value.getMonth() + 1);
}

function selectDate(date: string) {
  selectedDate.value = date;
}

function clearDateSelection() {
  selectedDate.value = null;
}

function goToSchedule(scheduleId: string) {
  console.log(`Navigate to /schedules/${scheduleId}`);
}

// 학습 진행 현황 API 호출
async function loadStudyProgress() {
  try {
    studyProgressLoading.value = true;
    studyProgressError.value = null;
    
    // 현재 진행 중인 스터디의 진행 현황 조회
    const response = await studyProgressApi.getCurrentStudyProgress();
    studyProgress.value = response;
    
    // 데이터 변환
    transformStudyProgressData();
  } catch (error) {
    console.error('학습 진행 현황 로드 실패:', error);
    
    // 현재 진행 중인 스터디가 없는 경우
    if (error.status === 404 && error.data?.message?.includes('현재 진행 중인 스터디가 없습니다')) {
      studyProgressError.value = '현재 진행 중인 스터디가 없습니다.';
    } else {
      studyProgressError.value = '학습 진행 현황을 불러오는데 실패했습니다.';
    }
  } finally {
    studyProgressLoading.value = false;
  }
}

// API 데이터를 UI 데이터로 변환
function transformStudyProgressData() {
  if (!studyProgress.value) return;
  
  // 스케줄을 섹션으로 변환
  const newSections: Section[] = studyProgress.value.schedules.map(schedule => ({
    id: schedule.scheduleId,
    title: schedule.scheduleName,
    isCompleted: schedule.sections.some(section => 
      section.members.some(member => member.reviewSubmitted)
    )
  }));
  
  sections.value = newSections;
  
  // 첫 번째 섹션을 기본 선택
  if (newSections.length > 0 && !selectedSection.value) {
    selectedSection.value = newSections[0];
  }
  
  // 선택된 섹션의 진행 상황 업데이트
  updateMemberProgress();
}

// 선택된 섹션의 팀원 진행 상황 업데이트
function updateMemberProgress() {
  if (!selectedSection.value || !studyProgress.value) return;
  
  const selectedSchedule = studyProgress.value.schedules.find(
    schedule => schedule.scheduleId === selectedSection.value!.id
  );
  
  if (!selectedSchedule) return;
  
  // 컬럼 헤더 생성
  const columns: string[] = [];
  selectedSchedule.sections.forEach(section => {
    if (section.needsReview) {
      columns.push(`섹션 ${section.sectionNumber} 회고`);
    }
  });
  
  // 심화학습 컬럼 추가
  if (selectedSchedule.hasDeepStudy) {
    columns.push('심화학습');
  }
  
  progressColumns.value = columns;
  
  // 팀원별 진행 상황 생성
  const memberMap = new Map<number, { name: string; progress: string[] }>();
  
  selectedSchedule.sections.forEach(section => {
    section.members.forEach(member => {
      if (!memberMap.has(member.userId)) {
        memberMap.set(member.userId, {
          name: member.userName,
          progress: new Array(columns.length).fill('x')
        });
      }
      
      const memberData = memberMap.get(member.userId)!;
      
      // 회고 제출 상태
      if (section.needsReview) {
        const reviewIndex = selectedSchedule.sections
          .filter(s => s.needsReview)
          .findIndex(s => s.sectionId === section.sectionId);
        if (reviewIndex !== -1) {
          memberData.progress[reviewIndex] = member.reviewSubmitted ? 'o' : 'x';
        }
      }
      
      // 심화학습 제출 상태
      if (selectedSchedule.hasDeepStudy) {
        const deepStudyIndex = columns.length - 1;
        memberData.progress[deepStudyIndex] = member.deepStudySubmitted ? 'o' : 'x';
      }
    });
  });
  
  // Map을 배열로 변환
  memberProgress.value = Array.from(memberMap.entries()).map(([id, data]) => ({
    id,
    name: data.name,
    progress: data.progress
  }));
}

function selectSection(section: Section) {
  selectedSection.value = section;
  updateMemberProgress();
}

// 컴포넌트 마운트 시 데이터 로드
onMounted(async () => {
  await loadStudyProgress();
});
</script>

<style scoped>
/* Remove number input arrows */
input[type="number"]::-webkit-inner-spin-button,
input[type="number"]::-webkit-outer-spin-button {
  -webkit-appearance: none;
  margin: 0;
}

input[type="number"] {
  -moz-appearance: textfield;
}
</style>
