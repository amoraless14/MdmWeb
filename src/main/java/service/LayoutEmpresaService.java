package service;

import Entidad.LayoutEmpresa;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import repository.LayoutEmpresaRepository;

import java.util.List;
import java.util.Optional;

@Service
public class LayoutEmpresaService {

    @Autowired
    private LayoutEmpresaRepository layoutEmpresaRepository;

    public List<LayoutEmpresa> obtenerTodos() {
        return layoutEmpresaRepository.findAll();
    }

    public LayoutEmpresa obtenerPorId(Long id) {
        return layoutEmpresaRepository.findById(id).orElse(null);
    }

    public LayoutEmpresa guardar(LayoutEmpresa layout) {
        return layoutEmpresaRepository.save(layout);
    }

    public void eliminar(Long id) {
        layoutEmpresaRepository.deleteById(id);
    }

}