package __TOP_LEVEL_PACKAGE__.util;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.filter.OncePerRequestFilter;
/*
 * Based on http://zhentao-li.blogspot.nl/2012/06/enable-cors-support-in-rest-services.html
 */
public class CorsFilter extends OncePerRequestFilter {

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		if (request.getHeader("Access-Control-Request-Method") != null && "OPTIONS".equals(request.getMethod())) {

			// CORS "pre-flight" request
			response.addHeader("Access-Control-Allow-Origin", "*");
			response.addHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE");
			response.addHeader("Access-Control-Allow-Headers", "Content-Type");
			response.addHeader("Access-Control-Max-Age", "3600");// 60 min
		} else if (isHeaderWithContentExists(request, "Accept", "application/json")
				|| isHeaderWithContentExists(request, "Content-Type", "application/json")) {
			response.addHeader("Access-Control-Allow-Origin", "*");
		}

		filterChain.doFilter(request, response);
	}

	private boolean isHeaderWithContentExists(HttpServletRequest request, String headerName, String contentToCheck) {
		return request.getHeader(headerName) != null && request.getHeader(headerName).contains(contentToCheck);
	}
}
