package br.com.alura.forum.config.security;

import br.com.alura.forum.modelo.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class TokenService {

    @Value("${forum.jwt.expiration}") //Pegando valor do application properties
    private String expiration;

    @Value("${forum.jwt.secret}") //Pegando valor do application properties
    private String secret;

    public String gerarToken(Authentication authentication) {

        Usuario logado = (Usuario) authentication.getPrincipal();
        Date hoje = new Date();
        Date dataExpiracao = new Date(hoje.getTime() + Long.parseLong(expiration));

        return Jwts.builder()
                .setIssuer("API do Forum da Alura") //Quem gerou o token
                .setSubject(logado.getId().toString()) //Quem é o usuario, o dono do token
                .setIssuedAt(hoje) //Data de geração do token
                .setExpiration(dataExpiracao) //Falta quanto tempo em mili segundo vai durar o token
                .signWith(SignatureAlgorithm.HS256, secret) //Gera uma senha com o secret e precisa dizer o algorito que gera essa senha
                .compact(); //Compacta todas essas infos no final
    }

    public boolean isTokenValid(String token) {
        try{ //Token valido
            Jwts.parser().setSigningKey(this.secret).parseClaimsJws(token); //Vai descriptografar o token e retornar as suas infos > Quando o token é valido ele retorna um objeto se for invalido ele joga uma exception
            return true;
        }
        catch (Exception e){ //Token invalido
            return false;
        }
    }

    public Long getIdUsuario(String token) {
        Claims claims = Jwts.parser().setSigningKey(this.secret).parseClaimsJws(token).getBody();
        return Long.parseLong(claims.getSubject()); //Pega o id usuario
    }
}
