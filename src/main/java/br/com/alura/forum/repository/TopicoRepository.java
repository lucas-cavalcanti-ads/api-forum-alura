package br.com.alura.forum.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.com.alura.forum.modelo.Topico;

//JpaRepository<Entidade/Classe que vai controlar no banco, tipo do Id da entidade/classe>
public interface TopicoRepository extends JpaRepository<Topico, Long>{

	//Manter esse padrão de nome do metodo, ele vai pegar o atributo nome da entidade curso que fica dentro de topico
	Page<Topico> findByCurso_Nome(String nomeCurso, Pageable paginacao);
	
	/*Quando você não quer deixar o nome padrao do spring
	@Query("SELECT t FROM Topico t WHERE t.curso.nome = :nomeCurso")
	List<Topico> buscarPorNomeCurso(@Param("nomeCurso") String nomeCurso);
	*/ 
	
}
