package com.protocol;

public class Protocol
{
	// 프로토콜 타입에 관한 변수
	public static final String PT_EXIT = "0"; // 프로그램 종료
	public static final String PT_REQ_LOGIN_INFO = "1"; // 로그인 정보 요청
	public static final String PT_REQ_LOGIN = "2"; // 로그인 요청
	public static final String PT_RES_LOGIN = "3"; // 로그인 요청 응답
	public static final String PT_REQ_VIEW = "4"; // 조회 요청
	public static final String PT_RES_VIEW = "5"; // 조회 요청 으답
	public static final String PT_REQ_RENEWAL = "6"; // 갱신 요청
	public static final String PT_RES_RENEWAL = "7"; // 갱신 요청 응답
	
	// 회원가입
	public static final String CS_REQ_SIGNUP = "6-0"; // 회원가입 요청
	public static final String SC_RES_SIGNUP = "7-0"; // 회원가입 요청 응답
	
	// 관리자
	// 회원 관리
	public static final String CS_REQ_MEMBER_VIEW = "4-13"; // 회원 정보 요청
	public static final String SC_RES_MEMBER_VIEW = "5-26"; // 회원 정보 요청 응답
	public static final String CS_REQ_CUSTOM_INFO = "4-14"; // 다중 정보 요청
	public static final String SC_RES_CUSTOM_INFO = "5-28"; // 다중 정보 요청 응답
	
	// 영화관 관리
	public static final String CS_REQ_THEATER_VIEW = "4-8"; // 영화관 정보 요청
	public static final String SC_RES_THEATER_VIEW = "5-10"; // 영화관 정보 요청 응답
	public static final String CS_REQ_THEATER_ADD = "6-4"; // 영화관 등록 요청
	public static final String SC_RES_THEATER_ADD = "7-8"; // 영화관 등록 요청 응답
	public static final String CS_REQ_THEATER_CHANGE = "6-5"; // 영화관 수정 요청
	public static final String SC_RES_THEATER_CHANGE = "7-A"; // 영화관 수정 요청 응답
	public static final String CS_REQ_THEATER_DELETE = "6-6"; // 영화관 삭제 요청
	public static final String SC_RES_THEATER_DELETE = "7-C"; // 영화관 삭제 요청 응답
	
	// 상영관 관리
	public static final String CS_REQ_SCREEN_VIEW = "4-11"; // 상영관 정보 요청
	public static final String SC_RES_SCREEN_VIEW = "5-22"; // 상영관 정보 요청 응답
	public static final String CS_REQ_SCREEN_ADD = "6-A"; // 상영관 등록 요청
	public static final String SC_RES_SCREEN_ADD = "7-14"; // 상영관 등록 요청 응답
	public static final String CS_REQ_SCREEN_CHANGE = "6-B"; // 상영관 수정 요청
	public static final String SC_RES_SCREEN_CHANGE = "7-16"; // 상영관 수정 요청 응답
	public static final String CS_REQ_SCREEN_DELETE = "6-C"; // 상영관 삭제 요청
	public static final String SC_RES_SCREEN_DELETE = "7-18"; // 상영관 삭제 요청
	
	// 영화 관리
	public static final String CS_REQ_MOVIE_VIEW = "4-D"; // 영화 정보 요청
	public static final String SC_RES_MOVIE_VIEW = "5-1A"; // 영화 정보 요청 읃답
	public static final String CS_REQ_MOVIE_ADD = "6-D"; // 영화 등록 요청
	public static final String SC_RES_MOVIE_ADD = "7-1A"; // 영화 등록 요청 응답
	public static final String CS_REQ_MOVIE_CHANGE = "6-E"; // 영화 수정 요청
	public static final String SC_RES_MOVIE_CHANGE = "7-1C"; // 영화 수정 요청 응답
	public static final String CS_REQ_MOVIE_DELETE = "6-F"; // 영화 삭제 요청
	public static final String SC_RES_MOVIE_DELETE = "7-1E"; // 영화 삭제 요청 응답
	
	// 예매정보 관리
	public static final String CS_REQ_RESERVATION_VIEW = "4-F"; // 관리자 예매 내역 요청
	public static final String SC_RES_RESERVATION_VIEW = "5-1E"; // 관리자 예매 내역 요청 응답
	public static final String CS_REQ_ADMINRESERVATION_ADD = "6-14"; // 관리자 예매 요청
	public static final String SC_RES_ADMINRESERVATION_ADD = "7-28"; // 관리자 예매 요청 응답
	
	// 상영시간표 관리
	public static final String CS_REQ_TIMETABLE_VIEW = "4-10"; // 관리자 상영시간표 요청
	public static final String SC_RES_TIMETABLE_VIEW = "5-20"; // 관리자 상영시간표 요청 응답
	public static final String CS_REQ_TIMETABLE_ADD = "6-7"; // 상영시간표 등록 요청
	public static final String SC_RES_TIMETABLE_ADD = "7-E"; // 상영시간표 등록 요청 응답
	public static final String CS_REQ_TIMETABLE_CHANGE = "6-8"; // 상영시간표 수정 요청
	public static final String SC_RES_TIMETABLE_CHANGE = "7-10"; // 상영시간표 수정 요청 응답
	public static final String CS_REQ_TIMETABLE_DELETE = "6-9"; // 상영시간표 삭제 요청
	public static final String SC_RES_TIMETALBE_DELETE = "7-12"; // 상영시간표 삭제 요청 응답
	
	// 통계정보
	public static final String CS_REQ_STATISTICS_VIEW = "4-9"; // 통계정보 요청
	public static final String SC_RES_STATISTICS_VIEW = "5-12"; // 통계정보 요청 응답
	
	// 수입계좌 관리
	public static final String CS_REQ_ACCOUNT_VIEW = "4-B"; // 수입계좌 정보 요청
	public static final String SC_RES_ACCOUNT_VIEW = "5-16"; // 수입계좌 정보 요청 응답
	public static final String CS_REQ_ACCOUNT_CHANGE = "6-12"; // 수입계좌 수정 요청
	public static final String SC_RES_ACCOUNT_CHANGE = "7-24"; // 수입계좌 수정 요청 응답
	
	// 가격정보 수정
	public static final String CS_REQ_PRICE_VIEW = "4-C"; // 시간대별 가격 정보 요청
	public static final String SC_RES_PRICE_VIEW = "5-18"; // 시간대별 가격 정보 요청 응답
	public static final String CS_REQ_PRICE_CHANGE = "6-13"; // 시간대별 가격 정보 요청
	public static final String SC_RES_PRICE_CHANGE = "7-26"; // 시간대별 가격 정보 요청 응답
	
	// 사용자
	// 영화관
	public static final String CS_REQ_THEATERDETAIL_VIEW = "4-13"; // 사용자 영화관 상세정보 요청
	public static final String SC_RES_THEATERDETAIL_VIEW = "5-26"; // 사용자 영화관 상세정보 요청 응답
	
	// 영화
	// 영화 검색은 관리자 프로토콜과 동일
	//public static final String CS_REQ_MOVIEPRESENT_VIEW = "4-4"; // 현재상영작 정보 요청
	//public static final String SC_RES_MOVIEPRESENT_VIEW = "5-8"; // 현재상영작 정보 요청 응답
	//public static final String CS_REQ_MOVIEFUTURE_VIEW = "4-5"; // 상영예정작 정보 요청
	//public static final String SC_RES_MOVIEFUTURE_VIEW = "5-A"; // 상영예정작 정보 요청 응답
	public static final String CS_REQ_MOVIEDETAIL_VIEW = "4-6"; // 영화 상세정보 요청
	public static final String SC_RES_MOVIEDETAIL_VIEW = "5-C"; // 영화 상세정보 요청 응답
	public static final String CS_REQ_MOVIESUB_VIEW = "4-4"; // 영화 서브 정보 요청
	public static final String SC_RES_MOVIESUB_VIEW = "5-8"; // 영화 서브 정보 요청 응답
	
	// 평점 및 관람평
	public static final String CS_REQ_REVIEW_VIEW = "4-7"; // 평점 및 관람평 요청
	public static final String SC_RES_REVIEW_VIEW = "5-E"; // 평점 및 관람평 요청 응답
	public static final String CS_REQ_REVIEW_ADD = "6-10"; // 평점 및 관람평 등록 요청
	public static final String SC_RES_REVIEW_ADD = "7-20"; // 평점 및 관람평 등록 요청 응답
	public static final String CS_REQ_REVIEW_DELETE = "6-11"; // 평점 및 관람평 삭제 요청
	public static final String SC_RES_REVIEW_DELETE = "7-22"; // 평점 및 관람평 삭제 요청 응답
	
	// 예매 - 상영시간표
	public static final String CS_REQ_THEATERLIST_VIEW = "4-0"; // 예매_영화관 리스트 요청
	public static final String SC_RES_THEATERLIST_VIEW = "5-0"; // 예매_영화관 리스트 요청 응답
	public static final String CS_REQ_MOVIELIST_VIEW = "4-1"; // 예매_영화 리스트 요청
	public static final String SC_RES_MOVIELIST_VIEW = "5-2"; // 예매_영화 리스트 요청 응답
	public static final String CS_REQ_TIMETABLELIST_VIEW = "4-2"; // 예매_상영시간표 리스트 요청
	public static final String SC_RES_TIMETABLELIST_VIEW = "5-4"; // 예매_상영시간표 리스트 요청 응답
	public static final String CS_REQ_SEATLIST_VIEW = "4-3"; // 예매_좌석 리스트 요청
	public static final String SC_RES_SEATLIST_VIEW = "5-6"; // 예매_좌석 리스트 요청 응답
	public static final String CS_REQ_RESERVATION_ADD = "6-1"; // 예매 등록 요청
	public static final String SC_RES_RESERVATION_ADD = "7-2"; // 예매 등록 요청 응답
	public static final String CS_REQ_PAYMENT_ADD = "6-3"; // 결제 요청
	public static final String SC_RES_PAYMENT_ADD = "7-6"; // 결제 요청 응답
	
	// 예매 현황
	public static final String CS_REQ_USERRESERVATION_VIEW = "4-E"; // 사용자 예매 내역 요청
	public static final String SC_RES_USERRESERVATION_VIEW = "5-1C"; // 사용자 예매 내역 요청 응답
	public static final String CS_REQ_RESERVATION_DELETE = "6-2"; // 예매 취소 요청
	public static final String SC_RES_RESERVATION_DELETE = "7-4"; // 예매 취소 요청 응답
}