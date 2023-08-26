package com.book.common.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.book.member.vo.MemberVO;
import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;


@Component
public class ViewNameInterceptor extends HandlerInterceptorAdapter {

	@Autowired
	private SqlSession sqlSession;

	@Autowired
	private MemberVO memberVO;
	

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

		HttpSession session = request.getSession();
		try {
			if(sqlSession==null) {
			}
			memberVO = (MemberVO) session.getAttribute("memberInfo");
			if(memberVO == null) {
			}else {
				String member_id = memberVO.getMember_id();

//				   장바구니 갯수 카운트
				int cartCount = 0;
				cartCount = sqlSession.selectOne("cartLen", member_id);
				session.setAttribute("cartCount", cartCount);
				
				int deliveringCount = 0;
				deliveringCount=sqlSession.selectOne("deliveringLen",member_id);
				session.setAttribute("deliveringCount", deliveringCount);
				if(member_id.equals("admin") == true) {
					int goodsLen = 0;
					goodsLen=sqlSession.selectOne("goodsLen");
					session.setAttribute("goodsLen", goodsLen);
//					
					int ordersLen = 0;
					ordersLen=sqlSession.selectOne("ordersLen");
//					ordersLen = countDAO.ordersLen();
					session.setAttribute("ordersLen", ordersLen);
//					
					int membersLen = 0;
					membersLen=sqlSession.selectOne("membersLen");
//					membersLen = countDAO.membersLen();
					session.setAttribute("membersLen", membersLen);
//					
					Long totalSales = 0L;
//					totalSales = (Long)countDAO.totalSales();
					totalSales=(Long)sqlSession.selectOne("totalSales");
					session.setAttribute("totalSales", totalSales);
					
				}
			}
			
			
		} catch (Exception e) {
		}
		try {
			String viewName = getViewName(request);
			request.setAttribute("viewName", viewName);
		} catch (Exception er) {
			er.printStackTrace();
		}
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
	}

	private String getViewName(HttpServletRequest request) throws Exception {
		String contextPath = request.getContextPath();
		String uri = (String) request.getAttribute("javax.servlet.include.request_uri");
		if (uri == null || uri.trim().equals("")) {
			uri = request.getRequestURI();
		}

		int begin = 0;
		if (!((contextPath == null) || ("".equals(contextPath)))) {
			begin = contextPath.length();
		}

		int end;
		if (uri.indexOf(";") != -1) {
			end = uri.indexOf(";");
		} else if (uri.indexOf("?") != -1) {
			end = uri.indexOf("?");
		} else {
			end = uri.length();
		}

		String fileName = uri.substring(begin, end);
		if (fileName.indexOf(".") != -1) {
			fileName = fileName.substring(0, fileName.lastIndexOf("."));
		}
		if (fileName.lastIndexOf("/") != -1) {
			fileName = fileName.substring(fileName.lastIndexOf("/", 1), fileName.length());
		}
		return fileName;
	}
}
