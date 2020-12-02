package com.protocol;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Timer;

import com.db.model.AccountDAO;
import com.db.model.AccountDTO;
import com.db.model.ChargeDAO;
import com.db.model.ChargeDTO;
import com.db.model.DAO;
import com.db.model.DAOException;
import com.db.model.DTO;
import com.db.model.MemberDAO;
import com.db.model.MemberDTO;
import com.db.model.MovieDAO;
import com.db.model.MovieDTO;
import com.db.model.ReservationDAO;
import com.db.model.ReservationDTO;
import com.db.model.ReviewDAO;
import com.db.model.ReviewDTO;
import com.db.model.ScreenDAO;
import com.db.model.ScreenDTO;
import com.db.model.TheaterDAO;
import com.db.model.TheaterDTO;
import com.db.model.TimeTableDAO;
import com.db.model.TimeTableDTO;

public class MovieServer extends Thread
{
	Socket socket;
	private static int currUser = 0;
	public static int cnt = 1;
	BufferedReader br = null;
	BufferedWriter bw = null;
	private int price;
	
	public MovieServer(Socket socket) throws ClassNotFoundException, SQLException
	{
		this.socket = socket;
		DAO.connectDB();
		System.out.println("현재 사용자 수 :" + ++currUser);
	}
	
	@Override
	public void run()
	{
		try
		{
			br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			
			writePacket(Protocol.PT_REQ_LOGIN_INFO); // 접속 시 로그인 정보 요청
			boolean program_stop = false;
			
			while (true)
			{
				String packet = br.readLine();
				String packetArr[] = packet.split("`");
				String packetType = packetArr[0]; // 프로토콜 타입 구분
				
				switch (packetType)
				{
					case Protocol.PT_EXIT: // 프로그램 종료 수신
					{
						writePacket(Protocol.PT_EXIT);
						program_stop = true;
						System.out.println("서버종료");
						break;
					}
					
					case Protocol.PT_REQ_LOGIN: // 로그인 정보 수신
					{
						System.out.println("클라이언트가  로그인 정보를 보냈습니다");
						String login_id = packetArr[1]; // 아이디
						String login_passwd = packetArr[2]; // 비밀번호
						try
						{
							MemberDAO mDao = new MemberDAO();
							MemberDTO mDto = mDao.getMember(login_id, login_passwd); // 아이디, 비밀번호와 일치하는 멤버DTO 반환
							
							if (mDto.getRole().equals("1"))
								writePacket(Protocol.PT_RES_LOGIN + "`1"); // 관리자
							else
								writePacket(Protocol.PT_RES_LOGIN + "`2"); // 사용자
							
							System.out.println("로그인 성공"); // 성공시 인터페이스 홈 접속
						}
						catch (Exception e)
						{
							writePacket(Protocol.PT_RES_LOGIN + "`3");
							System.out.println("로그인 실패"); // 실패시 메시지 창 출력 및 재입력 유도
						}
						break;
					}
					
					case Protocol.PT_REQ_VIEW: // 조회 요청
					{
						String packetCode = packetArr[1]; // 프로토콜 코드 구분
						
						switch (packetCode)
						{
							case Protocol.CS_REQ_MEMBER_VIEW: // 회원 리스트 조회
							{
								try
								{
									System.out.println("클라이언트가 회원 리스트 요청을 보냈습니다.");
									MemberDAO mDao = new MemberDAO();
									Iterator<MemberDTO> m_iter = mDao.getAllMember().iterator(); // 모든 회원 ArrayList 반환
									MemberDTO mDto;
									String memberList = ""; // 회원 리스트 저장할 문자열
									
									if (m_iter.hasNext() == false) // 회원이 존재하지 않을 경우 실패 전송
									{
										writePacket(Protocol.PT_RES_VIEW + "`" + Protocol.SC_RES_MEMBER_VIEW + "`2");
										break;
									}
									
									while (m_iter.hasNext())
									{
										mDto = m_iter.next();
										// 회원간에는 '{'로 구분, 회원정보는 '|'로 구분
										memberList += mDto.getId() + "|" + mDto.getName() + "|" + mDto.getPassword() + "|" + mDto.getRole() + "|" + mDto.getGender() + "|" + mDto.getPhoneNumber() + "|" + mDto.getBirth() + "|" + mDto.getAccount() + "{";
									}
									
									writePacket(Protocol.PT_RES_VIEW + "`" + Protocol.SC_RES_MEMBER_VIEW + "`1`" + memberList);
									System.out.println("회원 리스트 전송 성공");
									break;
								}
								catch (Exception e)
								{
									e.printStackTrace();
									writePacket(Protocol.PT_RES_VIEW + "`" + Protocol.SC_RES_MEMBER_VIEW + "`3");
									break;
								}
							}
							
							case Protocol.CS_REQ_THEATER_VIEW: // 영화관 리스트 조회
							{
								try
								{
									System.out.println("클라이언트가 영화관 리스트 요청을 보냈습니다.");
									TheaterDAO tDao = new TheaterDAO();
									ArrayList<TheaterDTO> tlist = tDao.getTheaterList(); // 모든 영화관 ArrayList 반환
									Iterator<TheaterDTO> tIter = tlist.iterator();
									TheaterDTO tDto;
									String theaterList = ""; // 영화관 리스트 저장할 문자열
									
									if (tIter.hasNext() == false) // 영화관이 존재하지 않을 경우 실패 전송
									{
										writePacket(Protocol.PT_RES_VIEW + "`" + Protocol.SC_RES_THEATER_VIEW + "`2");
										break;
									}
									
									while (tIter.hasNext())
									{
										tDto = tIter.next();
										// 영화관간에는 '{'로 구분, 영화관정보는 '|'로 구분
										theaterList += tDto.getId() + "|" + tDto.getName() + "|" + tDto.getAddress() + "|" + tDto.getTotalScreen() + "|" + tDto.getTotalSeats() + "{";
									}
									
									writePacket(Protocol.PT_RES_VIEW + "`" + Protocol.SC_RES_THEATER_VIEW + "`1`" + theaterList);
									System.out.println("영화관 리스트 전송 성공");
									break;
								}
								catch (Exception e)
								{
									e.printStackTrace();
									writePacket(Protocol.PT_RES_VIEW + "`" + Protocol.SC_RES_THEATER_VIEW + "`3");
									break;
								}
							}
							
							case Protocol.CS_REQ_SCREEN_VIEW: // 상영관 리스트 조회
							{
								try
								{
									System.out.println("클라이언트가 상영관 리스트 요청을 보냈습니다.");
									String id = packetArr[2]; // 영화관id
									
									ScreenDAO sDao = new ScreenDAO();
									ArrayList<ScreenDTO> slist = sDao.getScreenList(id); // 선택한 영화관에 해당하는 상영관 ArrayList 반환
									Iterator<ScreenDTO> sIter = slist.iterator();
									String screenList = ""; // 상영관 리스트 저장할 문자열
									
									if (sIter.hasNext() == false) // 상영관이 존재하지 않을 경우 실패 전송
									{
										writePacket(Protocol.PT_RES_VIEW + "`" + Protocol.SC_RES_SCREEN_VIEW + "`2");
										break;
									}
									
									while (sIter.hasNext())
									{
										ScreenDTO sDto = sIter.next();
										// 상영관간에는 '{'로 구분, 상영관정보는 '|'로 구분
										screenList += sDto.getId() + "|" + id + "|" + sDto.getName() + "|" + sDto.getTotalCapacity() + "|" + sDto.getMaxRow() + "|" + sDto.getMaxCol() + "{";
									}
									
									writePacket(Protocol.PT_RES_VIEW + "`" + Protocol.SC_RES_SCREEN_VIEW + "`1`" + screenList);
									System.out.println("상영관 리스트 전송 성공");
									break;
								}
								catch (Exception e)
								{
									e.printStackTrace();
									writePacket(Protocol.PT_RES_VIEW + "`" + Protocol.SC_RES_SCREEN_VIEW + "`3");
									break;
								}
							}
							
							case Protocol.CS_REQ_MOVIE_VIEW: // 영화 리스트 조회
							{
								try
								{
									System.out.println("클라이언트가 영화 리스트 요청을 보냈습니다.");
									HashMap<String, String> info = new HashMap<String, String>();
									info.put("title", packetArr[2]);	 	// 제목
									info.put("start_date", packetArr[3]);	// 개봉일
									info.put("end_date", packetArr[4]);		// 종료일
									info.put("is_current", packetArr[5]);	// 0:상영 종료, 1:현재 상영작, 2:상영 예정작
									info.put("director", packetArr[6]);		// 감독
									info.put("actor", packetArr[7]);		// 배우
									String type = packetArr[8]; // 1:현재 상영작 리스트, 2:상영 예정작 리스트, 그외:입력된 영화 정보로 조회
									
									MovieDAO tDao = new MovieDAO();
									ArrayList<MovieDTO> tlist;
									if (type.equals("1"))
										tlist = tDao.getCurrentMovieList(); // 현재 상영 리스트
									else if (type.equals("2"))
										tlist = tDao.getSoonMovieList(); // 상영 예정 리스트
									else
										tlist = tDao.getMovieList(info); // 키워드에 해당하는 리스트
										
									Iterator<MovieDTO> tIter = tlist.iterator();
									String movieList = ""; // 영화 리스트 저장할 문자열
									
									if (tIter.hasNext() == false) // 영화 존재하지 않을 경우 실패 전송
									{
										writePacket(Protocol.PT_RES_VIEW + "`" + Protocol.SC_RES_MOVIE_VIEW + "`2");
										break;
									}
									
									while (tIter.hasNext())
									{
										MovieDTO mDto = tIter.next();
										// 영화간에는 '{'로 구분, 영화정보는 '|'로 구분
										movieList += mDto.getId() + "|" + mDto.getTitle() + "|" + mDto.getReleaseDate() + "|" + mDto.getIsCurrent() + "|" + mDto.getPlot() + "|" + mDto.getPosterPath() + "|" + mDto.getStillCutPath() + "|" + mDto.getTrailerPath() + "|" + mDto.getDirector() + "|" + mDto.getActor() + "|" + Integer.toString(mDto.getMin()) + "{";
									}
									
									writePacket(Protocol.PT_RES_VIEW + "`" + Protocol.SC_RES_MOVIE_VIEW + "`1`" + movieList);
									System.out.println("영화 리스트 전송 성공");
									break;
								}
								catch (Exception e)
								{
									e.printStackTrace();
									writePacket(Protocol.PT_RES_VIEW + "`" + Protocol.SC_RES_MOVIE_VIEW + "`3");
									break;
								}
							}
							
							case Protocol.CS_REQ_ACCOUNT_VIEW: // 관리자 계좌 정보 조회
							{
								try
								{
									System.out.println("클라이언트가 계좌 정보 요청을 보냈습니다.");
									AccountDAO aDao = new AccountDAO();
									AccountDTO aDto = aDao.getAdminAccount("admin");
									
									String account = aDto.getAccount(); // 계좌
									String bank = aDto.getBank();		// 은행
									
									System.out.println("클라이언트에게 계좌 정보를 보냅니다.");
									writePacket(Protocol.PT_RES_VIEW + "`" + Protocol.SC_RES_ACCOUNT_VIEW + "`1`" + account + "`" + bank);
									break;
								}
								catch (Exception e)
								{
									e.printStackTrace();
									writePacket(Protocol.PT_RES_VIEW + "`" + Protocol.SC_RES_ACCOUNT_VIEW + "`2");
									break;
								}
							}
							
							case Protocol.CS_REQ_TIMETABLE_VIEW: // 상영시간표 조회
							{
								try
								{
									System.out.println("관리자가 상영시간표 요청을 보냈습니다.");
									String mov_id = packetArr[2];
									String screen_id = packetArr[3];
									String date = packetArr[4];
									String start_time = packetArr[5];
									String end_time = packetArr[6];
									String theater_id = packetArr[7];
									
									TimeTableDAO ttDao = new TimeTableDAO();
									ArrayList<TimeTableDTO> t_list;
									if (theater_id.equals("null"))
										t_list = ttDao.getTimeTableList(new TimeTableDTO(DTO.EMPTY_ID, mov_id, screen_id, date + start_time, date + end_time, "1", 0));
									else
										t_list = ttDao.getTimeTableList(new TimeTableDTO(DTO.EMPTY_ID, mov_id, screen_id, date + start_time, date + end_time, "1", 0), theater_id);
									
									Iterator<TimeTableDTO> t_iter = t_list.iterator();
									String timetableList = ""; // 상영시간표 저장할 문자열
									
									if (t_iter.hasNext() == false) // 상영시간표 존재하지 않읗 경우 실패 전송
									{
										writePacket(Protocol.PT_RES_VIEW + "`" + Protocol.SC_RES_TIMETABLE_VIEW + "`2");
										break;
									}
									
									while (t_iter.hasNext())
									{
										TimeTableDTO tbDto = t_iter.next();
										// 상영시간표간에는 '{'로 구분, 상영시간표정보는 '|'로 구분
										timetableList += tbDto.getId() + "|" + tbDto.getScreenId() + "|" + tbDto.getMovieId() + "|" + tbDto.getType() + "|" + tbDto.getCurrentRsv() + "|" + tbDto.getStartTime() + "|" + tbDto.getEndTime() + "{";
									}
									
									writePacket(Protocol.PT_RES_VIEW + "`" + Protocol.SC_RES_TIMETABLE_VIEW + "`1`" + timetableList);
									System.out.println("상영시간표 리스트 전송 성공");
									break;
								}
								catch (Exception e)
								{
									e.printStackTrace();
									writePacket(Protocol.PT_RES_VIEW + "`" + Protocol.SC_RES_TIMETABLE_VIEW + "`3");
									break;
								}
							}
							
							case Protocol.CS_REQ_CUSTOM_INFO:
							{
								try
								{
									String type = packetArr[2];
									String id_data = packetArr[3];
									String infoList = "";
									
									MovieDAO movDao = new MovieDAO();
									TheaterDAO tDao = new TheaterDAO();
									ScreenDAO sDao = new ScreenDAO();
									TimeTableDAO ttDao = new TimeTableDAO();
									MemberDAO memDao = new MemberDAO();
									
									switch (type)
									{
										case "0": // screen
										{
											TimeTableDTO ttDto = ttDao.getTimeTable(id_data);
											ScreenDTO screen = sDao.getScreenElem(ttDto.getScreenId());
											infoList += screen.getId() + "|" + screen.getTheaterId() + "|" + screen.getName() + "|" + Integer.toString(screen.getTotalCapacity()) + "|" + Integer.toString(screen.getMaxRow()) + "|" + Integer.toString(screen.getMaxCol());
											break;
										}
										case "1": // movie, screen, theater
										{
											TimeTableDTO ttDto = ttDao.getTimeTable(id_data);
											ScreenDTO screen = sDao.getScreenElem(ttDto.getScreenId());
											MovieDTO movie = movDao.getMovie(ttDto.getMovieId());
											TheaterDTO theater = tDao.getTheaterElem(screen.getTheaterId());
											
											infoList += screen.getId() + "|" + screen.getTheaterId() + "|" + screen.getName() + "|" + Integer.toString(screen.getTotalCapacity()) + "|" + Integer.toString(screen.getMaxRow()) + "|" + Integer.toString(screen.getMaxCol()) + "{";
											infoList += movie.getId() + "|" + movie.getTitle() + "|" + movie.getReleaseDate().toString() + "|" + movie.getIsCurrent() + "|" + movie.getPlot() + "|" + movie.getPosterPath() + "|" + movie.getStillCutPath() + "|" + movie.getTrailerPath() + "|" + movie.getDirector() + "|" + movie.getActor() + "|" + Integer.toString(movie.getMin()) + "{";
											infoList += theater.getId() + "|" + theater.getName() + "|" + theater.getAddress() + "|" + Integer.toString(theater.getTotalScreen()) + "|" + Integer.toString(theater.getTotalSeats());
											break;
										}
										case "2": // movie, screen, theater, member, titmetable
										{
											TimeTableDTO ttDto = ttDao.getTimeTable(id_data);
											ScreenDTO screen = sDao.getScreenElem(ttDto.getScreenId());
											MovieDTO movie = movDao.getMovie(ttDto.getMovieId());
											TheaterDTO theater = tDao.getTheaterElem(screen.getTheaterId());
											MemberDTO member = memDao.getMemberInfo(packetArr[4]);
											
											infoList += screen.getId() + "|" + screen.getTheaterId() + "|" + screen.getName() + "|" + Integer.toString(screen.getTotalCapacity()) + "|" + Integer.toString(screen.getMaxRow()) + "|" + Integer.toString(screen.getMaxCol()) + "{";
											infoList += movie.getId() + "|" + movie.getTitle() + "|" + movie.getReleaseDate().toString() + "|" + movie.getIsCurrent() + "|" + movie.getPlot() + "|" + movie.getPosterPath() + "|" + movie.getStillCutPath() + "|" + movie.getTrailerPath() + "|" + movie.getDirector() + "|" + movie.getActor() + "|" + Integer.toString(movie.getMin()) + "{";
											infoList += theater.getId() + "|" + theater.getName() + "|" + theater.getAddress() + "|" + Integer.toString(theater.getTotalScreen()) + "|" + Integer.toString(theater.getTotalSeats()) + "{";
											infoList += member.getId() + "|" + member.getRole() + "|" + member.getPassword() + "|" + member.getAccount() + "|" + member.getName() + "|" + member.getPhoneNumber() + "|" + member.getBirth() + "|" + member.getGender() + "{";
											infoList += ttDto.getId() + "|" + ttDto.getMovieId() + "|" + ttDto.getScreenId() + "|" + ttDto.getStartTime() + "|" + ttDto.getEndTime() + "|" + ttDto.getType() + "|" + Integer.toString(ttDto.getCurrentRsv());
											break;
										}
										case "3": // review, member
										{
											MemberDTO member = memDao.getMemberInfo(id_data);
											
											infoList += member.getId() + "|" + member.getRole() + "|" + member.getPassword() + "|" + member.getAccount() + "|" + member.getName() + "|" + member.getPhoneNumber() + "|" + member.getBirth() + "|" + member.getGender();
											break;
										}
									}
									
									writePacket(Protocol.PT_RES_VIEW + "`" + Protocol.SC_RES_CUSTOM_INFO + "`1`" + infoList);
									break;
								}
								catch (Exception e)
								{
									e.printStackTrace();
									writePacket(Protocol.PT_RES_VIEW + "`" + Protocol.SC_RES_CUSTOM_INFO + "`2");
									break;
								}
							}
							
							case Protocol.CS_REQ_RESERVATION_VIEW: // 예매 리스트 조회
							{
								try
								{
									System.out.println("클라이언트가 예매 리스트를 요청하였습니다.");
									String mem_id = packetArr[2]; // 회원id
									
									ReservationDAO rDao = new ReservationDAO();
									ArrayList<ReservationDTO> r_list;
									
									if (packetArr[3].equals("null")) // 회원id만 받을 경우 해당 회원 예매 리스트 조회
									{
										r_list = rDao.getRsvListFromMem(mem_id);
									}
									else // 추가 정보에 따른 예매 리스트 조회
									{
										String mov_id = packetArr[3];		
										String thea_id = packetArr[4];		
										String start_date = packetArr[5];
										String end_date = packetArr[6];
										r_list = rDao.getRsvList(mem_id, mov_id, thea_id, start_date + " 00:00:00.0", end_date + " 23:59:00.0");
									}
									
									Iterator<ReservationDTO> r_iter = r_list.iterator();
									String reservatinList = "";
									
									if (r_iter.hasNext() == false) // 예매 리스트 존재하지 않을 경우 실패 전송
									{
										writePacket(Protocol.PT_RES_VIEW + "`" + Protocol.SC_RES_RESERVATION_VIEW + "`2");
										break;
									}
									
									while (r_iter.hasNext())
									{
										ReservationDTO rDto = r_iter.next();
										// 예매리스트간에는 '{'로 구분, 예매정보는 '|'로 구분
										reservatinList += rDto.getId() + "|" + rDto.getMemberId() + "|" + rDto.getTimeTableId() + "|" + rDto.getScreenRow() + "|" + rDto.getScreenCol() + "|" + rDto.getPrice() + "|" + rDto.getType() + "|" + rDto.getRsvTime() + "|" + rDto.getAccount() + "|" + rDto.getBank() + "{";
									}
									
									writePacket(Protocol.PT_RES_VIEW + "`" + Protocol.SC_RES_RESERVATION_VIEW + "`1`" + reservatinList);
									System.out.println("예매 리스트 전송 성공");
									break;
								}
								catch (Exception e)
								{
									e.printStackTrace();
									writePacket(Protocol.PT_RES_VIEW + "`" + Protocol.SC_RES_RESERVATION_VIEW + "`2");
									break;
								}
							}
							
							case Protocol.CS_REQ_PRICE_VIEW: // 가격 정보 조회(조조, 일반, 심야)
							{
								try
								{
									System.out.println("클라이언트가 가격 정보 조회 요청을 보냈습니다.");
									ChargeDAO cDao = new ChargeDAO();
									ArrayList<ChargeDTO> cList = cDao.getChargeList();
									Iterator<ChargeDTO> cIter = cList.iterator();
									String priceList = ""; // 가격 정보 저장할 문자열
									
									if (cIter.hasNext() == false) // 가격 정보 존재하지 않을 경우 실패 전송
									{
										writePacket(Protocol.PT_RES_VIEW + "`" + Protocol.SC_RES_PRICE_VIEW + "`2");
										break;
									}
									
									while (cIter.hasNext())
									{
										ChargeDTO temp = cIter.next();
										String priceType = temp.getType(); // 가격타입(조조, 일반, 심야)
										String price = Integer.toString(temp.getPrice()); // 가격
										
										priceList += priceType + "|" + price + "{";
									}
									writePacket(Protocol.PT_RES_VIEW + "`" + Protocol.SC_RES_PRICE_VIEW + "`1`" + priceList);
									System.out.println("가격 정보 전송 성공");
									break;
								}
								catch (Exception e)
								{
									e.printStackTrace();
									writePacket(Protocol.PT_RES_VIEW + "`" + Protocol.SC_RES_PRICE_VIEW + "`2");
									break;
								}
							}
							
							case Protocol.CS_REQ_MOVIESUB_VIEW: // 현재상영작 및 상영예정작 조회 시 예매율과 평점 조회
							{
								try
								{
									System.out.println("클라이언트가 영화 서브 정보를 요청하였습니다.");
									String movieId = packetArr[2]; // 영화id
									
									TimeTableDAO tDao = new TimeTableDAO();
									ReviewDAO rDao = new ReviewDAO();
									double rsv_rate = tDao.getRsvRate(movieId); // 예매율
									
									if (Double.isNaN(rsv_rate))
										rsv_rate = 0;
									
									String aver_star = Integer.toString(rDao.getAverStarGrade(movieId)); // 평점
									
									writePacket(Protocol.PT_RES_VIEW + "`" + Protocol.SC_RES_MOVIESUB_VIEW + "`1`" + Double.toString(rsv_rate) + "`" + aver_star);
									System.out.println("영화별 영화 서브  정보 전송 성공");
									break;
								}
								catch (Exception e)
								{
									e.printStackTrace();
									writePacket(Protocol.PT_RES_VIEW + "`" + Protocol.SC_RES_MOVIESUB_VIEW + "`2");
									break;
								}
							}
							
							case Protocol.CS_REQ_REVIEW_VIEW: // 리뷰 조회
							{
								try
								{
									System.out.println("클라이언트가 리뷰 리스트를 요청하였습니다.");
									String movieId = packetArr[2]; // 영화id
									
									ReviewDAO rDao = new ReviewDAO();
									ArrayList<ReviewDTO> r_list = rDao.getRvListFromMov(movieId); // 영화id에 따른 리뷰 ArrayList
									Iterator<ReviewDTO> r_iter = r_list.iterator();
									String reviewList = "";
									
									if (r_iter.hasNext() == false) // 리뷰 존재하지 않을 경우
									{
										writePacket(Protocol.PT_RES_VIEW + "`" + Protocol.SC_RES_REVIEW_VIEW + "`2");
										break;
									}
									while (r_iter.hasNext())
									{
										ReviewDTO rDto = r_iter.next();
										// 리뷰리스트간에는 '{'로 구분, 리뷰정보는 '|'로 구분
										reviewList += rDto.getId() + "|" + rDto.getMemberId() + "|" + rDto.getMovieId() + "|" + rDto.getStar() + "|" + rDto.getText() + "|" + rDto.getWriteTime() + "{";
									}
									
									writePacket(Protocol.PT_RES_VIEW + "`" + Protocol.SC_RES_REVIEW_VIEW + "`1`" + reviewList);
									System.out.println("리뷰 리스트 전송 성공");
									break;
								}
								catch (Exception e)
								{
									e.printStackTrace();
									writePacket(Protocol.PT_RES_VIEW + "`" + Protocol.SC_RES_REVIEW_VIEW + "`2");
									break;
								}
							}
							
							case Protocol.CS_REQ_STATISTICS_VIEW: // 통계 정보 조회
							{
								try
								{
									System.out.println("클라이언트가 통계 정보를 요청하였습니다.");
									String start_date = packetArr[2]; // 시작일 
									String end_date = packetArr[3]; // 종료일
									String stt_list = ""; // 저장할 통계 정보 리스트
									
									ReservationDAO rDao = new ReservationDAO();
									ArrayList<String> benefit_list = rDao.getBenefitSatistics(start_date, end_date); 	// 수익률
									ArrayList<String> rsv_list = rDao.getRsvSatistics(start_date, end_date);			// 예매율
									ArrayList<String> cancle_list = rDao.getCancelSatistics(start_date, end_date);		// 취소율
									
									for (String benefit_info : benefit_list)
										stt_list += benefit_info + "|";
									stt_list += "{";
									
									for (String rsv_info : rsv_list)
										stt_list += rsv_info + "|";
									stt_list += "{";
									
									for (String cancle_info : cancle_list)
										stt_list += cancle_info + "|";
									
									writePacket(Protocol.PT_RES_VIEW + "`" + Protocol.SC_RES_STATISTICS_VIEW + "`1`" + stt_list);
									break;
								}
								catch (Exception e)
								{
									e.printStackTrace();
									writePacket(Protocol.PT_RES_VIEW + "`" + Protocol.SC_RES_STATISTICS_VIEW + "`2");
									break;
								}
							}
							
							case Protocol.CS_REQ_SEAT_VIEW: // 예매 시 좌석 정보 조회
							{
								try
								{
									System.out.println("클라이언트가 좌석 정보를 요청하였습니다.");
									String timetable_id = packetArr[2]; // 상영시간표id
									String row_list =""; // 예매된 행, 열 저장할 문자열
									String col_list ="";
									
									ReservationDAO rDao = new ReservationDAO();
									Iterator<ReservationDTO> r_iter = rDao.getRsvListFromTT(timetable_id).iterator(); // 상영시간표에 해당하는 예매 리스트
									
									while (r_iter.hasNext()) // 예매된 좌석 저장
						            {
						                ReservationDTO rDto = r_iter.next();
						                row_list += Integer.toString(rDto.getScreenRow()) + "|";
						                col_list += Integer.toString(rDto.getScreenCol()) + "|";
						            }
									
									writePacket(Protocol.PT_RES_VIEW + "`" + Protocol.SC_RES_SEAT_VIEW + "`1`" + row_list + "`" + col_list);
									System.out.println("좌석 정보 전송 성공");
									break;
								}
								catch (Exception e)
								{
									e.printStackTrace();
									writePacket(Protocol.PT_RES_VIEW + "`" + Protocol.SC_RES_SEAT_VIEW + "`2");
									break;
								}
							}
						}
					}
					
					case Protocol.PT_REQ_RENEWAL: // 갱신 요청
					{
						String packetCode = packetArr[1];
						
						switch (packetCode)
						{
							case Protocol.CS_REQ_SIGNUP: // 회원가입
							{
								try
								{
									System.out.println("클라이언트가  회원가입 정보를 보냈습니다");
									String role = packetArr[2]; // 구분(사용자, 관리자)
									String signUp_id = packetArr[3]; // 아이디
									String signUp_password = packetArr[4]; // 암호
									String name = packetArr[5]; // 이름
									String phone_number = packetArr[6]; // 연락처
									String birth = packetArr[7]; // 생년월일
									String gender = packetArr[8]; // 성별
									
									MemberDAO signUpDAO = new MemberDAO();
									signUpDAO.addMember(new MemberDTO(signUp_id, role, signUp_password, null, name, phone_number, birth, gender));
									System.out.println("회원가입 성공");
									writePacket(Protocol.PT_RES_RENEWAL + "`" + Protocol.SC_RES_SIGNUP + "`1");
									break;
								}
								catch (DAOException e)
								{
									e.printStackTrace();
									writePacket(Protocol.PT_RES_RENEWAL + "`" + Protocol.SC_RES_SIGNUP + "`2");
									break;
								}
							}
							
							case Protocol.CS_REQ_THEATER_ADD: // 영화관 추가
							{
								try
								{
									System.out.println("클라이언트가 영화관 등록 요청를 보냈습니다.");
									String name = packetArr[2]; // 영화관 id
									String address = packetArr[3]; // 영화관 주소
									String screen = packetArr[4]; // 총 스크린 수
									String seat = packetArr[5]; // 총 좌석 수
									
									TheaterDAO theaterDAO = new TheaterDAO();
									TheaterDTO theaterDTO = new TheaterDTO(DTO.EMPTY_ID, name, address, Integer.valueOf(screen), Integer.valueOf(seat));
									
									theaterDAO.addTheater(theaterDTO);
									
									System.out.println("영화관 등록 성공");
									writePacket(Protocol.PT_RES_RENEWAL + "`" + Protocol.SC_RES_THEATER_ADD + "`1");
									break;
								}
								catch (Exception e)
								{
									e.printStackTrace();
									writePacket(Protocol.PT_RES_RENEWAL + "`" + Protocol.SC_RES_THEATER_ADD + "`2");
									break;
								}
							}
							
							case Protocol.CS_REQ_THEATER_CHANGE: // 영화관 수정
							{
								try
								{
									System.out.println("클라이언트가 영화관 수정 요청를 보냈습니다.");
									String id = packetArr[2]; // 영화관 ID
									String name = packetArr[3]; // 영화관 이름
									String address = packetArr[4]; // 영화관 주소
									String screen = packetArr[5]; // 총 스크린 수
									String seat = packetArr[6]; // 총 좌석 수
									
									TheaterDAO theaterDAO = new TheaterDAO();
									TheaterDTO theaterDTO = new TheaterDTO(id, name, address, Integer.valueOf(screen), Integer.valueOf(seat));
									
									theaterDAO.changeTheater(theaterDTO);
									
									System.out.println("영화관 수정 성공");
									writePacket(Protocol.PT_RES_RENEWAL + "`" + Protocol.SC_RES_THEATER_CHANGE + "`1");
									break;
								}
								catch (Exception e)
								{
									e.printStackTrace();
									writePacket(Protocol.PT_RES_RENEWAL + "`" + Protocol.SC_RES_THEATER_CHANGE + "`2");
									break;
								}
							}
							
							case Protocol.CS_REQ_THEATER_DELETE: // 영화관 삭제
							{
								try
								{
									System.out.println("클라이언트가 영화관 삭제 요청을 보냈습니다.");
									String id = packetArr[2]; // 영화관 ID
									
									TheaterDAO theaterDAO = new TheaterDAO();
									theaterDAO.removeTheater(id);
									
									System.out.println("영화관 삭제 성공");
									writePacket(Protocol.PT_RES_RENEWAL + "`" + Protocol.SC_RES_THEATER_DELETE + "`1");
									break;
								}
								catch (Exception e)
								{
									e.printStackTrace();
									writePacket(Protocol.PT_RES_RENEWAL + "`" + Protocol.SC_RES_THEATER_DELETE + "`2");
									break;
								}
							}
							
							case Protocol.CS_REQ_SCREEN_ADD: // 상영관 추가
							{
								try
								{
									System.out.println("클라이언트가 상영관 등록 요청을 보냈습니다.");
									String theater_id = packetArr[2]; 	// 상영관id
									String name = packetArr[3];			// 상영관명
									String capacity = packetArr[4];		// 총 수용 인원
									String row = packetArr[5];			// 최대 행
									String col = packetArr[6];			// 최대 열
									
									ScreenDAO sDao = new ScreenDAO();
									ScreenDTO sDto = new ScreenDTO(DTO.EMPTY_ID, theater_id, name, Integer.valueOf(capacity), Integer.valueOf(row), Integer.valueOf(col));
									
									sDao.addScreen(sDto);
									
									System.out.println("상영관 등록 성공");
									writePacket(Protocol.PT_RES_RENEWAL + "`" + Protocol.SC_RES_SCREEN_ADD + "`1");
									break;
								}
								catch (Exception e)
								{
									e.printStackTrace();
									writePacket(Protocol.PT_RES_RENEWAL + "`" + Protocol.SC_RES_SCREEN_ADD + "`2");
									break;
								}
							}
							
							case Protocol.CS_REQ_SCREEN_CHANGE: // 상영관 수정
							{
								try
								{
									System.out.println("클라이언트가 상영관 수정 요청을 보냈습니다.");
									String id = packetArr[2]; // 상영관id
									String theater_id = packetArr[3]; // 영화관id
									String name = packetArr[4]; // 상영관명
									String capacity = packetArr[5]; // 총 수용 인원
									String row = packetArr[6]; // 최대 행
									String col = packetArr[7]; // 최대 열
									
									ScreenDAO sDao = new ScreenDAO();
									ScreenDTO sDto = new ScreenDTO(id, theater_id, name, Integer.valueOf(capacity), Integer.valueOf(row), Integer.valueOf(col));
									
									sDao.changeScreen(sDto);
									
									System.out.println("상영관 수정 성공");
									writePacket(Protocol.PT_RES_RENEWAL + "`" + Protocol.SC_RES_SCREEN_CHANGE + "`1");
									break;
								}
								catch (Exception e)
								{
									e.printStackTrace();
									writePacket(Protocol.PT_RES_RENEWAL + "`" + Protocol.SC_RES_SCREEN_CHANGE + "`2");
									break;
								}
							}
							
							case Protocol.CS_REQ_SCREEN_DELETE: // 상영관 삭제
							{
								try
								{
									System.out.println("클라이언트가 상영관 삭제 요청을 보냈습니다.");
									String id = packetArr[2]; // 상영관id
									
									ScreenDAO sDao = new ScreenDAO();
									sDao.removeScreen(id);
									
									System.out.println("상영관 삭제 성공");
									writePacket(Protocol.PT_RES_RENEWAL + "`" + Protocol.SC_RES_SCREEN_DELETE + "`1");
									break;
								}
								catch (Exception e)
								{
									e.printStackTrace();
									writePacket(Protocol.PT_RES_RENEWAL + "`" + Protocol.SC_RES_SCREEN_DELETE + "`2");
									break;
								}
							}
							
							case Protocol.CS_REQ_MOVIE_ADD: // 영화 등록
							{
								try
								{
									System.out.println("클라이언트가 영화 등록 요청을 보냈습니다.");
									String title = packetArr[2]; 		// 영화제목
									String release_date = packetArr[3]; // 개봉일
									String is_current = packetArr[4]; 	// 상영 상태
									String plot = packetArr[5]; 		// 줄거리
									String poster = packetArr[6];		// 포스터경로
									String stillCut = packetArr[7];		// 스틸컷경로
									String trailer = packetArr[8];		// 트레일러경로
									String director = packetArr[9];		// 감독
									String actor = packetArr[10];		// 배우
									String min = packetArr[11];			// 상영시간(분)
									
									// DTO에 데이터 삽입
									MovieDAO mDao = new MovieDAO();
									MovieDTO mDto = new MovieDTO(DTO.EMPTY_ID, title, release_date, is_current, plot, poster, stillCut, trailer, director, actor, Integer.valueOf(min));
									// DAO에서 영화 추가
									mDao.addMovie(mDto);
									
									System.out.println("영화 등록 성공");
									writePacket(Protocol.PT_RES_RENEWAL + "`" + Protocol.SC_RES_MOVIE_ADD + "`1");
									break;
								}
								catch (DAOException e)
								{
									e.printStackTrace();
									writePacket(Protocol.PT_RES_RENEWAL + "`" + Protocol.SC_RES_MOVIE_ADD + "`2");
									break;
								}
								catch (Exception e)
								{
									e.printStackTrace();
									writePacket(Protocol.PT_RES_RENEWAL + "`" + Protocol.SC_RES_MOVIE_ADD + "`3");
									break;
								}
							}
							
							case Protocol.CS_REQ_MOVIE_CHANGE: // 영화 수정
							{
								try
								{
									System.out.println("클라이언트가 영화 수정 요청을 보냈습니다.");
									// 각 필드들이 비어있는 지 판단한 후 데이터 집어넣음
									MovieDAO mDao = new MovieDAO();
									MovieDTO mDto = mDao.getMovie(packetArr[2]);
									mDto.setTitle(packetArr[3]);		// 영화제목
									mDto.setReleaseDate(packetArr[4]);	// 개봉일
									mDto.setIsCurrent(packetArr[5]);	// 상영상태
									mDto.setPlot(packetArr[6]);			// 줄거리
									mDto.setPosterPath(packetArr[7]);	// 포스터경로
									mDto.setStillCutPath(packetArr[8]);	// 스틸컷경로
									mDto.setTrailerPath(packetArr[9]);	// 트레일러경로
									mDto.setDirector(packetArr[10]);	// 감독
									mDto.setActor(packetArr[11]);		// 배우
									mDto.setMin(Integer.parseInt(packetArr[12])); // 상영시간(분)
									
									mDao.changeMovie(mDto);
									System.out.println("영화 수정 성공");
									writePacket(Protocol.PT_RES_RENEWAL + "`" + Protocol.SC_RES_MOVIE_CHANGE + "`1");
									break;
								}
								catch (Exception e)
								{
									e.printStackTrace();
									writePacket(Protocol.PT_RES_RENEWAL + "`" + Protocol.SC_RES_MOVIE_CHANGE + "`2");
									break;
								}
							}
							
							case Protocol.CS_REQ_MOVIE_DELETE: // 영화 삭제
							{
								try
								{
									System.out.println("클라이언트가 영화 삭제 요청을 보냈습니다.");
									String id = packetArr[2]; // 영화id
									
									MovieDAO mDao = new MovieDAO();
									mDao.removeMovie(id);
									
									System.out.println("영화 삭제 성공");
									writePacket(Protocol.PT_RES_RENEWAL + "`" + Protocol.SC_RES_MOVIE_DELETE + "`1");
									break;
								}
								catch (Exception e)
								{
									e.printStackTrace();
									writePacket(Protocol.PT_RES_RENEWAL + "`" + Protocol.SC_RES_MOVIE_DELETE + "`2");
									break;
								}
							}
							
							case Protocol.CS_REQ_ACCOUNT_CHANGE: // 수입계좌 수정
							{
								try
								{
									System.out.println("클라이언트가 수입계좌 정보 수정 요청을 보냈습니다.");
									String bank = packetArr[2]; // 은행
									String account = packetArr[3]; // 계좌번호
									AccountDAO aDao = new AccountDAO();
									aDao.changeAccountInfo("admin", bank, account);
									System.out.println("수입계좌 정보 수정 성공");
									writePacket(Protocol.PT_RES_RENEWAL + "`" + Protocol.SC_RES_ACCOUNT_CHANGE + "`1");
									break;
								}
								catch (Exception e)
								{
									e.printStackTrace();
									System.out.println("수입계좌 수정 실패");
									writePacket(Protocol.PT_RES_RENEWAL + "`" + Protocol.SC_RES_ACCOUNT_CHANGE + "`2");
									break;
								}
							}
							
							case Protocol.CS_REQ_ADMINRESERVATION_ADD: // 관리자 예매 등록
							{
								Connection conn = DAO.getConn();
								conn.setAutoCommit(false);
								Savepoint sp = conn.setSavepoint();
								try
								{
									System.out.println("관리자가 예매 등록 요청을 보냈습니다.");
									ReservationDAO rDao = new ReservationDAO();
									String member = packetArr[2];		// 회원id
									String timetable_id = packetArr[3];	// 상영시간표id
									String account = packetArr[6];		// 계좌번호
									String bank = packetArr[7];			// 은행
									
									ArrayList<Integer> rowArr = new ArrayList<Integer>();
									String row_list[] = packetArr[4].split("|"); // 선택한 행 리스트
									for (String row : row_list)
										rowArr.add(Integer.valueOf(row));
									
									ArrayList<Integer> colArr = new ArrayList<Integer>();
									String col_list[] = packetArr[5].split("|"); // 선택한 열 리스트
									for (String col : col_list)
										colArr.add(Integer.valueOf(col));
									
									price = rDao.addPreRsv(member, timetable_id, rowArr, colArr);
									rDao.addConfimRsv(member, timetable_id, rowArr, colArr, account, bank);
									conn.commit();
									
									System.out.println("관리자 예매 정보 등록 성공");
									writePacket(Protocol.PT_RES_RENEWAL + "`" + Protocol.SC_RES_ADMINRESERVATION_ADD + "`1");
									break;
								}
								catch (DAOException e)
								{
									if (e.getMessage().equals("DUPLICATE_RSV"))
									{
										writePacket(Protocol.PT_RES_RENEWAL + "`" + Protocol.SC_RES_ADMINRESERVATION_ADD + "`2");
										break;
									}
									if (e.getMessage().equals("NOT_SELECTED"))
									{
										writePacket(Protocol.PT_RES_RENEWAL + "`" + Protocol.SC_RES_ADMINRESERVATION_ADD + "`3");
										break;
									}
								}
								catch (Exception e)
								{
									e.printStackTrace();
									conn.rollback(sp);
									conn.setAutoCommit(true);
									writePacket(Protocol.PT_RES_RENEWAL + "`" + Protocol.SC_RES_ADMINRESERVATION_ADD + "`4");
									break;
								} finally
								{
									conn.setAutoCommit(true);
								}
							}
							
							case Protocol.CS_REQ_RESERVATION_ADD: // 회원 예매
							{
								Connection conn = DAO.getConn();
								conn.setAutoCommit(false);
								Savepoint sp = conn.setSavepoint();
								try
								{
									System.out.println("회원이 예매 등록 요청을 보냈습니다.");
									ReservationDAO rDao = new ReservationDAO();
									String member_id = packetArr[2];	// 회원id
									String timetable_id = packetArr[3];	// 상영시간표id
									
									ArrayList<Integer> rowArr = new ArrayList<Integer>();
									String row_list[] = packetArr[4].split("|"); // 선택한 행 리스트
									for (String row : row_list)
										rowArr.add(Integer.valueOf(row));
									
									ArrayList<Integer> colArr = new ArrayList<Integer>();
									String col_list[] = packetArr[5].split("|"); // 선택한 열 리스트
									for (String col : col_list)
										colArr.add(Integer.valueOf(col));
									
									price = rDao.addPreRsv(member_id, timetable_id, rowArr, colArr);
									conn.commit();
									
									Timer m_timer = new Timer();
									ClearTimer m_task = new ClearTimer(member_id, timetable_id, rowArr, colArr);
									
									m_timer.schedule(m_task, 60000);
									
									System.out.println("회원 예매 성공");
									writePacket(Protocol.PT_RES_RENEWAL + "`" + Protocol.SC_RES_RESERVATION_ADD + "`1`" + price);
									break;
								}
								catch (Exception e)
								{
									conn.rollback(sp);
									e.printStackTrace();
									writePacket(Protocol.PT_RES_RENEWAL + "`" + Protocol.SC_RES_RESERVATION_ADD + "`2");
									break;
								} finally
								{
									conn.setAutoCommit(true);
								}
							}
							
							case Protocol.CS_REQ_RESERVATION_DELETE: // 예매 취소
							{
								Connection conn = DAO.getConn();
								conn.setAutoCommit(false);
								Savepoint sp = conn.setSavepoint();
								try
								{
									System.out.println("클라이언트가 예매 취소 요청을 보냈습니다.");
									String rsv_id = packetArr[2]; // 예매id
									ReservationDAO rDao = new ReservationDAO();
									rDao.cancelRsv(rsv_id); // 예매 취소
									rDao.refund(rsv_id); // 환불
									conn.commit();
									System.out.println("예매 취소 성공");
									writePacket(Protocol.PT_RES_RENEWAL + "`" + Protocol.SC_RES_RESERVATION_DELETE + "`1");
									break;
								}
								catch (Exception e)
								{
									conn.rollback(sp);
									e.printStackTrace();
									System.out.println("예매 취소 실패");
									writePacket(Protocol.PT_RES_RENEWAL + "`" + Protocol.SC_RES_RESERVATION_DELETE + "`2");
									break;
								} finally
								{
									conn.setAutoCommit(true);
								}
							}
							
							case Protocol.CS_REQ_TIMETABLE_ADD: // 상영시간표 추가
							{
								try
								{
									System.out.println("클라이언트가 상영시간표 등록을 요청했습니다.");
									String mv_id = packetArr[2]; // 영화id
									String sc_id = packetArr[3]; // 상영관id
									String start_time = packetArr[4]; // 시작시간
									String end_time = packetArr[5]; // 종료시간
									
									TimeTableDAO ttDao = new TimeTableDAO();
									ttDao.addTimeTable(new TimeTableDTO(DTO.EMPTY_ID, mv_id, sc_id, start_time, end_time, "0", 0));
									
									System.out.println("상영시간표 등록 성공");
									writePacket(Protocol.PT_RES_RENEWAL + "`" + Protocol.SC_RES_TIMETABLE_ADD + "`1");
									break;
								}
								catch (DAOException e)
								{
									if (e.getMessage().equals("DUPLICATE_TIMETABLE"))
									{
										e.printStackTrace();
										writePacket(Protocol.PT_RES_RENEWAL + "`" + Protocol.SC_RES_TIMETABLE_ADD + "`2");
										break;
									}
								}
								catch (Exception e)
								{
									e.printStackTrace();
									writePacket(Protocol.PT_RES_RENEWAL + "`" + Protocol.SC_RES_TIMETABLE_ADD + "`3");
									break;
								}
							}
							
							case Protocol.CS_REQ_TIMETABLE_CHANGE: // 상영시간표 수정
							{
								try
								{
									System.out.println("클라이언트가 상영시간표 수정을 요청했습니다.");
									String tt_id = packetArr[2]; // 상영시간표id
									String sc_id = packetArr[3]; // 상영관id
									String mv_id = packetArr[4]; // 영화id
									String start_time = packetArr[5]; // 시작시간
									String end_time = packetArr[6]; // 종료시간
									
									TimeTableDAO ttDao = new TimeTableDAO();
									ttDao.changeTimeTable(new TimeTableDTO(tt_id, mv_id, sc_id, start_time, end_time, "0", 0));
									
									System.out.println("상영시간표 수정 성공");
									writePacket(Protocol.PT_RES_RENEWAL + "`" + Protocol.SC_RES_TIMETABLE_CHANGE + "`1");
									break;
								}
								catch (DAOException e)
								{
									if (e.getMessage().equals("DUPLICATE_TIMETABLE"))
									{
										e.printStackTrace();
										writePacket(Protocol.PT_RES_RENEWAL + "`" + Protocol.SC_RES_TIMETABLE_CHANGE + "`2");
										break;
									}
								}
								catch (Exception e)
								{
									e.printStackTrace();
									writePacket(Protocol.PT_RES_RENEWAL + "`" + Protocol.SC_RES_TIMETABLE_CHANGE + "`3");
									break;
								}
							}
							
							case Protocol.CS_REQ_TIMETABLE_DELETE: // 상영시간표 삭제
							{
								try
								{
									System.out.println("클라이언트가 상영시간표 삭제를 요청했습니다.");
									String timetable_id = packetArr[2]; // 상영시간표id
									
									TimeTableDAO ttDao = new TimeTableDAO();
									ttDao.removeTimeTable(timetable_id);
									
									System.out.println("상영시간표 삭제 성공");
									writePacket(Protocol.PT_RES_RENEWAL + "`" + Protocol.SC_RES_TIMETALBE_DELETE + "`1");
									break;
								}
								catch (Exception e)
								{
									e.printStackTrace();
									writePacket(Protocol.PT_RES_RENEWAL + "`" + Protocol.SC_RES_TIMETALBE_DELETE + "`2");
									break;
								}
							}
							
							case Protocol.CS_REQ_PAYMENT_ADD: // 결제
							{
								Connection conn = DAO.getConn();
								conn.setAutoCommit(false);
								Savepoint sp = conn.setSavepoint();
								try
								{
									System.out.println("클라이언트가 결제를 요청했습니다.");
									
									ReservationDAO rDao = new ReservationDAO();
									String member_id = packetArr[2]; // 회원id
									String timetable_id = packetArr[3]; // 상영시간표id
									String account = packetArr[6]; // 계좌번호
									String bank = packetArr[7]; // 은행
									String passwd = packetArr[8]; // 비밀번호
									int price = 0;
									
									ArrayList<Integer> rowArr = new ArrayList<Integer>();
									String row_list[] = packetArr[4].split(","); // 선택 행 리스트
									for (String row : row_list)
										rowArr.add(Integer.valueOf(row));
									
									ArrayList<Integer> colArr = new ArrayList<Integer>();
									String col_list[] = packetArr[5].split(","); // 선택 열 리스트
									for (String col : col_list)
										colArr.add(Integer.valueOf(col));
									
									rDao.addConfimRsv(member_id, timetable_id, rowArr, colArr, account, bank);
									rDao.payment(account, bank, passwd, price);
									
									conn.commit();
									
									writePacket(Protocol.PT_RES_RENEWAL + "`" + Protocol.SC_RES_PAYMENT_ADD + "`1");
									break;
								}
								catch (Exception e)
								{
									if (e.getMessage().equals("PAYMENT_ERR"))
										writePacket(Protocol.PT_RES_RENEWAL + "`" + Protocol.SC_RES_PAYMENT_ADD + "`2");
									else if (e.getMessage().equals("NOT_SELECTED"))
										writePacket(Protocol.PT_RES_RENEWAL + "`" + Protocol.SC_RES_PAYMENT_ADD + "`3");
									else
										writePacket(Protocol.PT_RES_RENEWAL + "`" + Protocol.SC_RES_PAYMENT_ADD + "`4");
									
									e.printStackTrace();
									conn.rollback(sp);
									break;
								} finally
								{
									conn.setAutoCommit(true);
								}
							}
							
							case Protocol.CS_REQ_PRICE_CHANGE: // 가격 정보 수정
							{
								try
								{
									System.out.println("클라이언트가 가격 정보 수정 요청을 보냈습니다.");
									String morning = packetArr[2]; // 조조
									String afternoon = packetArr[3]; // 일반
									String night = packetArr[4]; // 심야
									
									ChargeDAO cDao = new ChargeDAO();
									cDao.changeCharge(new ChargeDTO("1", Integer.valueOf(morning)));
									cDao.changeCharge(new ChargeDTO("2", Integer.valueOf(afternoon)));
									cDao.changeCharge(new ChargeDTO("3", Integer.valueOf(night)));
									System.out.println("가격정보 수정 성공");
									writePacket(Protocol.PT_RES_RENEWAL + "`" + Protocol.SC_RES_PRICE_CHANGE + "`1");
									break;
								}
								catch (Exception e)
								{
									e.printStackTrace();
									System.out.println("가격정보 수정 실패");
									writePacket(Protocol.PT_RES_RENEWAL + "`" + Protocol.SC_RES_PRICE_CHANGE + "`2");
									break;
								}
							}
							
							case Protocol.CS_REQ_REVIEW_ADD: // 리뷰 등록
							{
								try
								{
									System.out.println("클라이언트가 리뷰 등록 요청을 보냈습니다.");
									ReviewDAO rDao = new ReviewDAO();
									String rv_id = packetArr[2]; // 리뷰id
									String rv_memId = packetArr[3]; // 회원id
									String rv_movId = packetArr[4]; // 영화id
									int rv_star = Integer.parseInt(packetArr[5]); // 평점
									String rv_text = packetArr[6]; // 리뷰 내용
									String rv_time = packetArr[7]; // 작성시간
									rDao.addReview(new ReviewDTO(rv_id, rv_memId, rv_movId, rv_star, rv_text, rv_time));
									
									System.out.println("리뷰 등록 성공");
									writePacket(Protocol.PT_RES_RENEWAL + "`" + Protocol.SC_RES_REVIEW_ADD + "`1");
									break;
								}
								catch (Exception e)
								{
									e.printStackTrace();
									writePacket(Protocol.PT_RES_RENEWAL + "`" + Protocol.SC_RES_REVIEW_ADD + "`2");
									break;
								}
							}
							
							case Protocol.CS_REQ_REVIEW_DELETE: // 리뷰 삭제
							{
								try
								{
									System.out.println("클라이언트가 리뷰 삭제 요청을 보냈습니다.");
									ReviewDAO rDao = new ReviewDAO();
									String rv_id = packetArr[2]; // 리뷰id
									rDao.removeReview(rv_id);
									
									System.out.println("리뷰 삭제 성공");
									writePacket(Protocol.PT_RES_RENEWAL + "`" + Protocol.SC_RES_REVIEW_DELETE + "`1");
								}
								catch (Exception e)
								{
									e.printStackTrace();
									writePacket(Protocol.PT_RES_RENEWAL + "`" + Protocol.SC_RES_REVIEW_DELETE + "`2");
									break;
								}
							}
						}
					}
				}// end switch
				
				if (program_stop)
					break;
			} // end while
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		try
		{
			// 접속 종료
			currUser--;
			socket.close();
			System.out.println("[" + socket.getInetAddress() + ":" + socket.getPort() + "]" + "가 접속종료 하였습니다.");
			System.out.println("현재 사용자 수:" + currUser);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public void writePacket(String source) throws Exception
	{
		try
		{
			bw.write(source + "\n");
			bw.flush();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public Socket getSocket()
	{
		return socket;
	}
}