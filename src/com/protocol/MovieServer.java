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
			
			writePacket(Protocol.PT_REQ_LOGIN_INFO);
			boolean program_stop = false;
			
			while (true)
			{
				String packet = br.readLine();
				String packetArr[] = packet.split("`");
				String packetType = packetArr[0];
				
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
						String login_id = packetArr[1];
						String login_passwd = packetArr[2];
						try
						{
							MemberDAO mDao = new MemberDAO();
							MemberDTO mDto = mDao.getMember(login_id, login_passwd);
							
							if (mDto.getRole().equals("1"))
								writePacket(Protocol.PT_RES_LOGIN + "`1");
							else
								writePacket(Protocol.PT_RES_LOGIN + "`2");
							System.out.println("로그인 성공"); // 성공시 인터페이스 홈 접속
							
						}
						catch (Exception e)
						{
							writePacket(Protocol.PT_RES_LOGIN + "`3");
							System.out.println("로그인 실패"); // 실패시 메시지 창 출력 및 재입력 유도
						}
						break;
					}
					
					case Protocol.PT_REQ_VIEW:
					{
						String packetCode = packetArr[1];
						
						switch (packetCode)
						{
							case Protocol.CS_REQ_MEMBER_VIEW:
							{
								try
								{
									System.out.println("클라이언트가 회원 리스트 요청을 보냈습니다.");
									MemberDAO mDao = new MemberDAO();
									Iterator<MemberDTO> m_iter = mDao.getAllMember().iterator();
									MemberDTO mDto;
									String memberList = "";
									
									if (m_iter.hasNext() == false)
									{
										writePacket(Protocol.PT_RES_VIEW + "`" + Protocol.SC_RES_MEMBER_VIEW + "`2");
										break;
									}
									
									while (m_iter.hasNext())
									{
										mDto = m_iter.next();
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
							
							case Protocol.CS_REQ_THEATER_VIEW:
							{
								try
								{
									System.out.println("클라이언트가 영화관 리스트 요청을 보냈습니다.");
									TheaterDAO tDao = new TheaterDAO();
									ArrayList<TheaterDTO> tlist = tDao.getTheaterList();
									Iterator<TheaterDTO> tIter = tlist.iterator();
									TheaterDTO tDto;
									String theaterList = ""; // 영화관리스트 정보를 모두 담을 문자열
									
									if (tIter.hasNext() == false)
									{
										writePacket(Protocol.PT_RES_VIEW + "`" + Protocol.SC_RES_THEATER_VIEW + "`2");
										break;
									}
									
									while (tIter.hasNext())
									{
										tDto = tIter.next();
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
							
							case Protocol.CS_REQ_SCREEN_VIEW:
							{
								try
								{
									System.out.println("클라이언트가 상영관 리스트 요청을 보냈습니다.");
									String id = packetArr[2];
									
									ScreenDAO sDao = new ScreenDAO();
									ArrayList<ScreenDTO> slist = sDao.getScreenList(id);
									Iterator<ScreenDTO> sIter = slist.iterator();
									String screenList = ""; // 상영관 리스트 모두 담을 문자열
									
									if (sIter.hasNext() == false)
									{
										writePacket(Protocol.PT_RES_VIEW + "`" + Protocol.SC_RES_SCREEN_VIEW + "`2");
										break;
									}
									
									while (sIter.hasNext())
									{
										ScreenDTO sDto = sIter.next();
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
							
							case Protocol.CS_REQ_MOVIE_VIEW:
							{
								try
								{
									System.out.println("클라이언트가 영화 리스트 요청을 보냈습니다.");
									HashMap<String, String> info = new HashMap<String, String>();
									info.put("title", packetArr[2]);
									info.put("start_date", packetArr[3]);
									info.put("end_date", packetArr[4]);
									info.put("is_current", packetArr[5]);
									info.put("director", packetArr[6]);
									info.put("actor", packetArr[7]);
									String type = packetArr[8];
									
									MovieDAO tDao = new MovieDAO();
									ArrayList<MovieDTO> tlist;
									if (type.equals("1"))
										tlist = tDao.getCurrentMovieList(); // 현재 상영 리스트
									else if (type.equals("2"))
										tlist = tDao.getSoonMovieList(); // 상영 예정 리스트
									else
										tlist = tDao.getMovieList(info); // 키워드에 해당하는 리스트
										
									Iterator<MovieDTO> tIter = tlist.iterator();
									String movieList = ""; // 영화 리스트 모두 담을 문자열
									
									if (tIter.hasNext() == false)
									{
										writePacket(Protocol.PT_RES_VIEW + "`" + Protocol.SC_RES_MOVIE_VIEW + "`2");
										break;
									}
									
									while (tIter.hasNext())
									{
										MovieDTO mDto = tIter.next();
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
							
							case Protocol.CS_REQ_ACCOUNT_VIEW:
							{
								try
								{
									System.out.println("클라이언트가 계좌 정보 요청을 보냈습니다.");
									AccountDAO aDao = new AccountDAO();
									AccountDTO aDto = aDao.getAdminAccount("admin");
									
									String account = aDto.getAccount();
									String bank = aDto.getBank();
									
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
							
							case Protocol.CS_REQ_TIMETABLE_VIEW:
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
									String timetableList = "";
									
									if (t_iter.hasNext() == false)
									{
										writePacket(Protocol.PT_RES_VIEW + "`" + Protocol.SC_RES_TIMETABLE_VIEW + "`2");
										break;
									}
									
									while (t_iter.hasNext())
									{
										TimeTableDTO tbDto = t_iter.next();
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
							
							case Protocol.CS_REQ_RESERVATION_VIEW:
							{
								try
								{
									System.out.println("클라이언트가 예매 리스트를 요청하였습니다.");
									String mem_id = packetArr[2];
									
									ReservationDAO rDao = new ReservationDAO();
									ArrayList<ReservationDTO> r_list;
									
									if (packetArr[3].equals("null"))
									{
										r_list = rDao.getRsvListFromMem(mem_id);
									}
									else
									{
										String mov_id = packetArr[3];
										String thea_id = packetArr[4];
										String start_date = packetArr[5];
										String end_date = packetArr[6];
										r_list = rDao.getRsvList(mem_id, mov_id, thea_id, start_date + " 00:00:00.0", end_date + " 23:59:00.0");
									}
									
									Iterator<ReservationDTO> r_iter = r_list.iterator();
									String reservatinList = "";
									
									if (r_iter.hasNext() == false)
									{
										writePacket(Protocol.PT_RES_VIEW + "`" + Protocol.SC_RES_RESERVATION_VIEW + "`2");
										break;
									}
									
									while (r_iter.hasNext())
									{
										ReservationDTO rDto = r_iter.next();
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
							
							case Protocol.CS_REQ_PRICE_VIEW:
							{
								try
								{
									System.out.println("클라이언트가 가격 정보 조회 요청을 보냈습니다.");
									ChargeDAO cDao = new ChargeDAO();
									ArrayList<ChargeDTO> cList = cDao.getChargeList();
									Iterator<ChargeDTO> cIter = cList.iterator();
									String priceList = "";
									
									if (cIter.hasNext() == false)
									{
										writePacket(Protocol.PT_RES_VIEW + "`" + Protocol.SC_RES_PRICE_VIEW + "`2");
										break;
									}
									
									while (cIter.hasNext())
									{
										ChargeDTO temp = cIter.next();
										String priceType = temp.getType();
										String price = Integer.toString(temp.getPrice());
										
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
							
							case Protocol.CS_REQ_MOVIESUB_VIEW:
							{
								try
								{
									System.out.println("클라이언트가 영화 서브 정보를 요청하였습니다.");
									String movieId = packetArr[2];
									
									TimeTableDAO tDao = new TimeTableDAO();
									ReviewDAO rDao = new ReviewDAO();
									double rsv_rate = tDao.getRsvRate(movieId);
									
									if (Double.isNaN(rsv_rate))
										rsv_rate = 0;
									
									String aver_star = Integer.toString(rDao.getAverStarGrade(movieId));
									
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
							
							case Protocol.CS_REQ_REVIEW_VIEW:
							{
								try
								{
									System.out.println("클라이언트가 리뷰 리스트를 요청하였습니다.");
									String movieId = packetArr[2];
									
									ReviewDAO rDao = new ReviewDAO();
									ArrayList<ReviewDTO> r_list = rDao.getRvListFromMov(movieId);
									Iterator<ReviewDTO> r_iter = r_list.iterator();
									String reviewList = "";
									
									if (r_iter.hasNext() == false)
									{
										writePacket(Protocol.PT_RES_VIEW + "`" + Protocol.SC_RES_REVIEW_VIEW + "`2");
										break;
									}
									while (r_iter.hasNext())
									{
										ReviewDTO rDto = r_iter.next();
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
							
							case Protocol.CS_REQ_STATISTICS_VIEW:
							{
								try
								{
									System.out.println("클라이언트가 통계 정보를 요청하였습니다.");
									String start_date = packetArr[2];
									String end_date = packetArr[3];
									String stt_list = "";
									
									ReservationDAO rDao = new ReservationDAO();
									ArrayList<String> benefit_list = rDao.getBenefitSatistics(start_date, end_date);
									ArrayList<String> rsv_list = rDao.getRsvSatistics(start_date, end_date);
									ArrayList<String> cancle_list = rDao.getCancelSatistics(start_date, end_date);
									
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
							
							case Protocol.CS_REQ_SEAT_VIEW:
							{
								try
								{
									System.out.println("클라이언트가 좌석 정보를 요청하였습니다.");
									String timetable_id = packetArr[2];
									String row_list = "";
									String col_list = "";
									
									ReservationDAO rDao = new ReservationDAO();
									Iterator<ReservationDTO> r_iter = rDao.getRsvListFromTT(timetable_id).iterator();
									
									while (r_iter.hasNext())
									{
										ReservationDTO rDto = r_iter.next();
										row_list += Integer.toString(rDto.getScreenRow()) + "|";
										col_list += Integer.toString(rDto.getScreenCol()) + "|";
									}
									
									writePacket(Protocol.PT_RES_VIEW + "`" + Protocol.SC_RES_SEAT_VIEW + "`1`" + row_list + "`" + col_list);
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
					
					case Protocol.PT_REQ_RENEWAL:
					{
						String packetCode = packetArr[1];
						
						switch (packetCode)
						{
							case Protocol.CS_REQ_SIGNUP:
							{
								try
								{
									System.out.println("클라이언트가  회원가입 정보를 보냈습니다");
									String role = packetArr[2]; // 구분(사용자, 관리자)
									String signUp_id = packetArr[3]; // 아이디
									String signUp_password = packetArr[4]; // 암호
									String name = packetArr[5]; // 이름
									String phone_number = packetArr[6]; // 연락처
									String birth = packetArr[7];
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
							
							case Protocol.CS_REQ_THEATER_ADD:
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
							
							case Protocol.CS_REQ_THEATER_CHANGE:
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
							
							case Protocol.CS_REQ_THEATER_DELETE:
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
							
							case Protocol.CS_REQ_SCREEN_ADD:
							{
								try
								{
									System.out.println("클라이언트가 상영관 등록 요청을 보냈습니다.");
									String theater_id = packetArr[2];
									String name = packetArr[3];
									String capacity = packetArr[4];
									String row = packetArr[5];
									String col = packetArr[6];
									
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
							
							case Protocol.CS_REQ_SCREEN_CHANGE:
							{
								try
								{
									System.out.println("클라이언트가 상영관 수정 요청을 보냈습니다.");
									String id = packetArr[2];
									String theater_id = packetArr[3];
									String name = packetArr[4];
									String capacity = packetArr[5];
									String row = packetArr[6];
									String col = packetArr[7];
									
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
							
							case Protocol.CS_REQ_SCREEN_DELETE:
							{
								try
								{
									System.out.println("클라이언트가 상영관 삭제 요청을 보냈습니다.");
									String id = packetArr[2];
									
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
							
							case Protocol.CS_REQ_MOVIE_ADD:
							{
								try
								{
									System.out.println("클라이언트가 영화 등록 요청을 보냈습니다.");
									String title = packetArr[2];
									String release_date = packetArr[3];
									String is_current = packetArr[4];
									String plot = packetArr[5];
									String poster = packetArr[6];
									String stillCut = packetArr[7];
									String trailer = packetArr[8];
									String director = packetArr[9];
									String actor = packetArr[10];
									String min = packetArr[11];
									
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
							
							case Protocol.CS_REQ_MOVIE_DELETE:
							{
								try
								{
									System.out.println("클라이언트가 영화 삭제 요청을 보냈습니다.");
									String id = packetArr[2];
									
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
							
							case Protocol.CS_REQ_ACCOUNT_CHANGE:
							{
								try
								{
									System.out.println("클라이언트가 수입계좌 정보 수정 요청을 보냈습니다.");
									String bank = packetArr[2];
									String account = packetArr[3];
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
							
							case Protocol.CS_REQ_MOVIE_CHANGE:
							{
								try
								{
									System.out.println("클라이언트가 영화 수정 요청을 보냈습니다.");
									// 각 필드들이 비어있는 지 판단한 후 데이터 집어넣음
									MovieDAO mDao = new MovieDAO();
									MovieDTO mDto = mDao.getMovie(packetArr[2]);
									mDto.setTitle(packetArr[3]);
									mDto.setReleaseDate(packetArr[4]);
									mDto.setIsCurrent(packetArr[5]);
									mDto.setPlot(packetArr[6]);
									mDto.setPosterPath(packetArr[7]);
									mDto.setStillCutPath(packetArr[8]);
									mDto.setTrailerPath(packetArr[9]);
									mDto.setDirector(packetArr[10]);
									mDto.setActor(packetArr[11]);
									mDto.setMin(Integer.parseInt(packetArr[12]));
									
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
							
							case Protocol.CS_REQ_ADMINRESERVATION_ADD:
							{
								Connection conn = DAO.getConn();
								conn.setAutoCommit(false);
								Savepoint sp = conn.setSavepoint();
								try
								{
									System.out.println("관리자가 예매 등록 요청을 보냈습니다.");
									ReservationDAO rDao = new ReservationDAO();
									String member = packetArr[2];
									String timetable_id = packetArr[3];
									String account = packetArr[6];
									String bank = packetArr[7];
									
									ArrayList<Integer> rowArr = new ArrayList<Integer>();
									String row_list[] = packetArr[4].split("|");
									for (String row : row_list)
										rowArr.add(Integer.valueOf(row));
									
									ArrayList<Integer> colArr = new ArrayList<Integer>();
									String col_list[] = packetArr[5].split("|");
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
								}
								finally
								{
									conn.setAutoCommit(true);
								}
							}
							
							case Protocol.CS_REQ_RESERVATION_ADD:
							{
								Connection conn = DAO.getConn();
								conn.setAutoCommit(false);
								Savepoint sp = conn.setSavepoint();
								try
								{
									System.out.println("회원이 예매 등록 요청을 보냈습니다.");
									ReservationDAO rDao = new ReservationDAO();
									String member_id = packetArr[2];
									String timetable_id = packetArr[3];
									
									ArrayList<Integer> rowArr = new ArrayList<Integer>();
									String row_list[] = packetArr[4].split("|");
									for (String row : row_list)
										rowArr.add(Integer.valueOf(row));
									
									ArrayList<Integer> colArr = new ArrayList<Integer>();
									String col_list[] = packetArr[5].split("|");
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
								}
								finally
								{
									conn.setAutoCommit(true);
								}
							}
							
							case Protocol.CS_REQ_RESERVATION_DELETE:
							{
								Connection conn = DAO.getConn();
								conn.setAutoCommit(false);
								Savepoint sp = conn.setSavepoint();
								try
								{
									System.out.println("클라이언트가 예매 취소 요청을 보냈습니다.");
									String rsv_id = packetArr[2];
									ReservationDAO rDao = new ReservationDAO();
									rDao.cancelRsv(rsv_id);
									rDao.refund(rsv_id);
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
								}
								finally
								{
									conn.setAutoCommit(true);
								}
							}
							
							case Protocol.CS_REQ_TIMETABLE_ADD:
							{
								try
								{
									System.out.println("클라이언트가 상영시간표 등록을 요청했습니다.");
									String mv_id = packetArr[2];
									String sc_id = packetArr[3];
									String start_time = packetArr[4];
									String end_time = packetArr[5];
									
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
							
							case Protocol.CS_REQ_TIMETABLE_CHANGE:
							{
								try
								{
									System.out.println("클라이언트가 상영시간표 수정을 요청했습니다.");
									String tt_id = packetArr[2];
									String sc_id = packetArr[3];
									String mv_id = packetArr[4];
									String start_time = packetArr[5];
									String end_time = packetArr[6];
									
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
							
							case Protocol.CS_REQ_TIMETABLE_DELETE:
							{
								try
								{
									System.out.println("클라이언트가 상영시간표 삭제를 요청했습니다.");
									String timetable_id = packetArr[2];
									
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
							
							case Protocol.CS_REQ_PAYMENT_ADD:
							{
								Connection conn = DAO.getConn();
								conn.setAutoCommit(false);
								Savepoint sp = conn.setSavepoint();
								try
								{
									System.out.println("클라이언트가 결제를 요청했습니다.");
									
									ReservationDAO rDao = new ReservationDAO();
									String member_id = packetArr[2];
									String timetable_id = packetArr[3];
									String account = packetArr[6];
									String bank = packetArr[7];
									String passwd = packetArr[8];
									int price = 0;
									
									ArrayList<Integer> rowArr = new ArrayList<Integer>();
									String row_list[] = packetArr[4].split(",");
									for (String row : row_list)
										rowArr.add(Integer.valueOf(row));
									
									ArrayList<Integer> colArr = new ArrayList<Integer>();
									String col_list[] = packetArr[5].split(",");
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
								}
								finally
								{
									conn.setAutoCommit(true);
								}
							}
							
							case Protocol.CS_REQ_PRICE_CHANGE:
							{
								try
								{
									System.out.println("클라이언트가 가격 정보 수정 요청을 보냈습니다.");
									String morning = packetArr[2];
									String afternoon = packetArr[3];
									String night = packetArr[4];
									
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
							
							case Protocol.CS_REQ_REVIEW_ADD:
							{
								try
								{
									System.out.println("클라이언트가 리뷰 등록 요청을 보냈습니다.");
									ReviewDAO rDao = new ReviewDAO();
									String rv_id = packetArr[2];
									String rv_memId = packetArr[3];
									String rv_movId = packetArr[4];
									int rv_star = Integer.parseInt(packetArr[5]);
									String rv_text = packetArr[6];
									String rv_time = packetArr[7];
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
							
							case Protocol.CS_REQ_REVIEW_DELETE:
							{
								try
								{
									System.out.println("클라이언트가 리뷰 삭제 요청을 보냈습니다.");
									ReviewDAO rDao = new ReviewDAO();
									String rv_id = packetArr[2];
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