package com.sys.kanri.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    @Value("${jwt.refresh-expiration}")
    private long refreshExpiration;

    private SecretKey secretKey;
    private io.jsonwebtoken.JwtParser jwtParser;

    @PostConstruct
    public void init() {
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
        this.jwtParser = Jwts.parser()
                .verifyWith(secretKey)
                .build();
    }

    /**
     * Trích xuất username từ JWT.
     * @param token Chuỗi JWT cần phân tích.
     * @return Username được trích xuất.
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Trích xuất một claim bất kỳ từ JWT.
     * Sử dụng JwtParser được khởi tạo sẵn để tối ưu hiệu suất.
     * @param token Chuỗi JWT.
     * @param claimsResolver Hàm để lấy claim mong muốn từ Claims.
     * @param <T> Kiểu dữ liệu của claim.
     * @return Claim được trích xuất.
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        try {
            final Claims claims = jwtParser.parseSignedClaims(token).getPayload();
            return claimsResolver.apply(claims);
        } catch (JwtException e) {
            // Xử lý các lỗi liên quan đến JWT tại đây (ví dụ: token hết hạn, chữ ký không khớp)
            // Có thể throw một exception tùy chỉnh hoặc trả về null.
            return null;
        }
    }

    /**
     * Generates an access token with optional extra claims and a specified username.
     * The token is built using the configured JWT expiration time.
     *
     * @param extraClaims Additional claims to include in the token.
     * @param username The username to associate with the token.
     * @return A signed JWT access token as a string.
     */
    public String generateAccessToken(Map<String, Object> extraClaims, String username) {
        return buildToken(extraClaims, username, jwtExpiration);
    }

    /**
     * Generates a signed JWT refresh token associated with the specified username.
     * The token is built using the configured refresh token expiration time.
     *
     * @param username The username to associate with the refresh token.
     * @return A signed JWT refresh token as a string.
     */
    public String generateRefreshToken(String username) {
        return buildToken(new HashMap<>(), username, refreshExpiration);
    }

    /**
     * Sinh JWT mới với các claim tùy chỉnh.
     * @param extraClaims Các claims bổ sung.
     * @param username Tên người dùng.
     * @return Chuỗi JWT đã được ký.
     */
    public String buildToken(Map<String, Object> extraClaims, String username, long expiration) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .claims(extraClaims)
                .subject(username)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(secretKey)
                .compact();
    }

    /**
     * Kiểm tra token hợp lệ (username và thời gian).
     * @param token Chuỗi JWT.
     * @param username Tên người dùng để so sánh.
     * @return true nếu token hợp lệ, false nếu không.
     */
    public boolean isTokenValid(String token, String username) {
        try {
            final String extractedUsername = extractUsername(token);
            return extractedUsername != null && extractedUsername.equals(username) && !isTokenExpired(token);
        } catch (JwtException e) {
            return false;
        }
    }

    private boolean isTokenExpired(String token) {
        Date expiration = extractClaim(token, Claims::getExpiration);
        return expiration != null && expiration.before(new Date());
    }

    public long getExpiration() {
        return jwtExpiration;
    }
}