package br.com.alura.forum.controller;

import java.net.URI;
import java.util.Optional;

import br.com.alura.forum.controller.dto.DetalhesDoTopicoDto;
import br.com.alura.forum.controller.form.AtualizacaoTopicoForm;
import br.com.alura.forum.controller.form.TopicoForm;
import br.com.alura.forum.repository.CursoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import br.com.alura.forum.controller.dto.TopicoDto;
import br.com.alura.forum.modelo.Topico;
import br.com.alura.forum.repository.TopicoRepository;
import org.springframework.web.util.UriComponentsBuilder;

import javax.transaction.Transactional;
import javax.validation.Valid;

@RestController
@RequestMapping("/topicos")
public class TopicosController {
	
	@Autowired
	private TopicoRepository topicoRepository;

	@Autowired
	private CursoRepository cursoRepository;

	@GetMapping
	@Cacheable(value = "listaDeTopicos") //Utilizar em tabelas que são raramente utilizadas, EX: Lista de Paises, Lista de Estados,
	public Page<TopicoDto> lista(@RequestParam(required = false) String nomeCurso,
								 @PageableDefault(sort = "id", direction = Sort.Direction.DESC, page = 0, size = 10) Pageable paginacao) { //Ex: http://localhost:8080/topicos?pagina=1&qtd=1&nomeCurso=Spring+Boot
		if(nomeCurso == null) {
			Page<Topico> topicos = topicoRepository.findAll(paginacao);
			return TopicoDto.converter(topicos);
		}else {
			Page<Topico> topicos = topicoRepository.findByCurso_Nome(nomeCurso, paginacao);
			return TopicoDto.converter(topicos);
		}			
	}

	@PostMapping
	@Transactional //Commita no Banco
	@CacheEvict(value = "listaDeTopicos", allEntries = true) //Limpa o Cache
	public ResponseEntity<TopicoDto> cadastrar(@RequestBody @Valid TopicoForm form, UriComponentsBuilder uriBuilder) { //O Valid é o Bean Validations conforme anotações da classe Topico Form
		Topico topico = form.converter(cursoRepository);
		topicoRepository.save(topico);

		//Monta a localização do recurso que foi criado
		URI uri = uriBuilder.path("/topicos/{id}").buildAndExpand(topico.getId()).toUri();

		return ResponseEntity.created(uri).body(new TopicoDto(topico));
	}

	@GetMapping("/{id}")
	public ResponseEntity<DetalhesDoTopicoDto> detalhar(@PathVariable Long id){ //Diz que vem na uri e não como parametro GET

		//Verificando se existe um registro no banco
		Optional<Topico> topicoOptional = topicoRepository.findById(id);
		if(topicoOptional.isPresent())
			return ResponseEntity.ok(new DetalhesDoTopicoDto(topicoOptional.get()));

		return ResponseEntity.notFound().build();

	}

	@PutMapping("/{id}")
	@Transactional //Commita no Banco
	@CacheEvict(value = "listaDeTopicos", allEntries = true) //Limpa o Cache
	public ResponseEntity<TopicoDto> atualizar(@PathVariable Long id, @RequestBody @Valid AtualizacaoTopicoForm form){
		//Verificando se existe um registro no banco
		Optional<Topico> topicoOptional = topicoRepository.findById(id);
		if(topicoOptional.isPresent()){
			Topico topico = form.atualizar(id, topicoRepository);
			TopicoDto topicoDto = new TopicoDto(topico);
			return ResponseEntity.ok(topicoDto);
		}

		return ResponseEntity.notFound().build();

	}

	@DeleteMapping("/{id}")
	@Transactional //Commita no Banco
	@CacheEvict(value = "listaDeTopicos", allEntries = true) //Limpa o Cache
	public ResponseEntity<String> remover(@PathVariable Long id){

		//Verificando se existe um registro no banco
		Optional<Topico> topicoOptional = topicoRepository.findById(id);
		if(topicoOptional.isPresent()){
			topicoRepository.deleteById(id);
			return ResponseEntity.ok("Tópico exluido com sucesso");
		}

		return ResponseEntity.notFound().build();
	}

}
