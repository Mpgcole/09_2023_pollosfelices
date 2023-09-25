package com.sinensia.pollosfelices.presentation.controllers;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.sinensia.pollosfelices.business.model.Producto;
import com.sinensia.pollosfelices.business.services.ProductoServices;
import com.sinensia.pollosfelices.presentation.config.PresentationException;

@RestController
@RequestMapping("/productos")
public class ProductoController {

	@Autowired
	private ProductoServices productoServices;
	
	@GetMapping
	public List<Producto> getAll(@RequestParam(value="texto", required=false) String texto){
		
		List<Producto> productos;
		
		if (texto == null) {
			productos = productoServices.getAll();
		} else {
			productos = productoServices.getByNombreLikeIgnoreCase(texto);
		}
		
		return productos;
	}
	
	@GetMapping("/{codigo}")
	public Producto getByCodigo(@PathVariable Long codigo) {
		
		Producto producto = productoServices.read(codigo).orElse(null);
	
		if(producto == null) {
			throw new PresentationException("No existe el producto " + codigo, HttpStatus.NOT_FOUND);
		} 
			
		return producto;
		
	}
	
	@PostMapping
	public ResponseEntity<?> create(@RequestBody Producto producto, UriComponentsBuilder ucb){
		
		Long codigo = productoServices.create(producto);
		
		URI uri = ucb.path("/productos/{codigo}").build(codigo);
		
		return ResponseEntity.created(uri).build();
	}
	
	@DeleteMapping("/{codigo}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable Long codigo){
		
		Optional<Producto> optional = productoServices.read(codigo);
		
		if(optional.isEmpty()) {
			throw new PresentationException("No existe el producto con id " + codigo + ". No se ha podido eliminar.", HttpStatus.NOT_FOUND);
		}
		
		productoServices.delete(codigo);
		
	}
	
	@PutMapping
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void update(@RequestBody Producto producto) {
		
		productoServices.update(producto);
		
	}
	
}
