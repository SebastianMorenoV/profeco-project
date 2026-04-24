/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.itson.gateway_consumidor.controller;

import com.mycompany.grpc.catalogo.BuscarRequest;
import com.mycompany.grpc.catalogo.CatalogoServiceGrpc;
import com.mycompany.grpc.catalogo.ListaProductosResponse;
import com.mycompany.grpc.catalogo.Producto;
import java.util.LinkedHashMap;
import java.util.List; 
import java.util.Map;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author josee
 */

 @RestController
 @RequestMapping("/api/productos")
public class ProductosController {
       @GrpcClient("ms-catalogo")
      private CatalogoServiceGrpc.CatalogoServiceBlockingStub catalogoStub;

    
      @GetMapping
      public ResponseEntity<?> buscarProductos(@RequestParam(value = "q", required = false, defaultValue = "") String
  query) {
          try {
              BuscarRequest request = BuscarRequest.newBuilder()
                      .setQuery(query)
                      .build();

              ListaProductosResponse response = catalogoStub.buscarProductos(request);

             
              List<Map<String, Object>> productosJson = response.getProductosList().stream()
                      .map(this::toJson)
                      .toList();

              Map<String, Object> body = new LinkedHashMap<>();
              body.put("status", "OK");
              body.put("query", query);
              body.put("total", productosJson.size());
              body.put("productos", productosJson);

              return ResponseEntity.ok(body);
          } catch (Exception e) {
              return ResponseEntity.internalServerError().body(Map.of(
                      "status", "ERROR",
                      "error", e.getMessage()
              ));
          }
      }

      private Map<String, Object> toJson(Producto p) {
          Map<String, Object> m = new LinkedHashMap<>();
          m.put("id", p.getId());
          m.put("nombre", p.getNombre());
          m.put("precio", p.getPrecio());
          m.put("supermercado", p.getSupermercado());
          return m;
      }
}
