package com.exchange.currencyexchangebackend.config;


import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.exchange.currencyexchangebackend.model.entity.Company;
import com.exchange.currencyexchangebackend.model.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.security.Key;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
public class JwtUtil {
    private Key secretKey;
    private final long accessTokenValidity = 60 * 60 * 1000; // 1 hour in milliseconds

    private JwtParser jwtParser;

    @PostConstruct
    public void init() {
        // Generate a secure key for HS256
//        this.secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
//        this.jwtParser = Jwts.parserBuilder().setSigningKey(secretKey).build();
        this.secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS512);
        this.jwtParser = Jwts.parserBuilder().setSigningKey(secretKey).build();

    }

    public String createToken(User user) {
        Claims claims = Jwts.claims().setSubject(user.getEmail());

        // المعلومات الأساسية للمستخدم
        claims.put("id", user.getId());
        claims.put("email", user.getEmail());
        claims.put("role", user.getRole());
        claims.put("company", user.getCompany());

//        claims.put("permissions", user.getPermissions()); // إذا كانت متوفرة

        // إضافة معلومات الجهاز والجلسة
        claims.put("ip", getCurrentIpAddress());
        claims.put("userAgent", getUserAgent());
        Date tokenCreateTime = new Date();
        Date tokenValidity = new Date(tokenCreateTime.getTime() + TimeUnit.DAYS.toMillis(1));

        return Jwts.builder()
                .setClaims(claims)
                .setIssuer("your-application-name")    // الجهة المصدرة للتوكن
                .setIssuedAt(tokenCreateTime)          // وقت الإصدار
                .setExpiration(tokenValidity)          // وقت الانتهاء
                .signWith(secretKey, SignatureAlgorithm.HS512) // خوارزمية التوقيع - استخدم HS512 للأمان العالي
                .compact();
    }

    // طرق مساعدة للحصول على معلومات إضافية
    private String getCurrentIpAddress() {
        // احصل على عنوان IP الحالي من طلب HTTP
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        return request.getRemoteAddr();
    }

    private String getUserAgent() {
        // احصل على معلومات المتصفح
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        return request.getHeader("User-Agent");
    }

    private Claims parseJwtClaims(String token) {
        return jwtParser.parseClaimsJws(token).getBody();
    }

    public Claims resolveClaims(HttpServletRequest req) {
        try {
            String token = resolveToken(req);
            if (token != null) {
                return parseJwtClaims(token);
            }
            return null;
        } catch (Exception ex) {
            req.setAttribute("invalid", ex.getMessage());
            throw ex;
        }
    }

    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    public boolean validateClaims(Claims claims) {
        return claims.getExpiration().after(new Date());
    }

    public String getEmail(Claims claims) {
        return claims.getSubject();
    }

    public static Long extractUserId(String cleantoken) {

        String token = cleantoken.replace("Bearer ", "");

        try {
            DecodedJWT jwt = JWT.decode(token);

            return jwt.getClaim("id").asLong();
        } catch (Exception e) {
            System.err.println("Erreur lors du décodage du token: " + e.getMessage());
            return null;
        }
    }

    public static Company extractCompany(String cleantoken) {

        String token = cleantoken.replace("Bearer ", "");

        try {
            DecodedJWT jwt = JWT.decode(token);

            return jwt.getClaim("company").as(Company.class);
        } catch (Exception e) {
            System.err.println("Erreur lors du décodage du token: " + e.getMessage());
            return null;
        }
    }

}
