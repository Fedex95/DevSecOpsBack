package com.tienda.kpback.Controller;

import com.tienda.kpback.Entity.Historial;
import com.tienda.kpback.Service.HistorialService;
import com.tienda.kpback.Service.NotificacionesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("historial")


public class HistorialController {
    @Autowired
    private HistorialService historialService;


    @GetMapping("/{usuarioId}")
    public ResponseEntity<List<Historial>> getHistorial(@PathVariable Long usuarioId){
        List<Historial> historials = historialService.findByUsuarioId(usuarioId);
        return ResponseEntity.ok(historials);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Historial>> getAllHistorials(@RequestParam Long userId){
        try{
            List<Historial> historials = historialService.getHistorialAdmin(userId);
            return ResponseEntity.ok(historials);
        }catch (RuntimeException e){
            return ResponseEntity.status(403).build();
        }
    }
}
