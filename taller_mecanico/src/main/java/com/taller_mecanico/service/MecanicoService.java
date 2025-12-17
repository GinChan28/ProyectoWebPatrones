package com.taller_mecanico.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.taller_mecanico.domain.Mecanico;
import com.taller_mecanico.domain.Cita;
import com.taller_mecanico.domain.Usuario;
import com.taller_mecanico.domain.Rol;
import com.taller_mecanico.domain.UsuarioRol;
import com.taller_mecanico.repository.MecanicoRepository;
import com.taller_mecanico.repository.CitaRepository;
import com.taller_mecanico.repository.UsuarioRepository;
import com.taller_mecanico.repository.RolRepository;
import com.taller_mecanico.repository.UsuarioRolRepository;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MecanicoService {

    private final MecanicoRepository mecanicoRepository;
    private final CitaRepository citaRepository;
    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final UsuarioRolRepository usuarioRolRepository;
    private final PasswordEncoder passwordEncoder;

    public MecanicoService(
            MecanicoRepository mecanicoRepository,
            CitaRepository citaRepository,
            UsuarioRepository usuarioRepository,
            RolRepository rolRepository,
            UsuarioRolRepository usuarioRolRepository,
            PasswordEncoder passwordEncoder) {

        this.mecanicoRepository = mecanicoRepository;
        this.citaRepository = citaRepository;
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.usuarioRolRepository = usuarioRolRepository;
        this.passwordEncoder = passwordEncoder;
    }
@Transactional
public void guardar(Mecanico mecanico) {

    if (mecanico.getIdMecanico() == null) {
        // CREAR
        String username = mecanico.getCedula();

        if (usuarioRepository.existsByUsername(username)) {
            throw new IllegalStateException(
                "Ya existe un usuario con esa cédula / username"
            );
        }

        Usuario usuario = new Usuario();
        usuario.setUsername(username);
        usuario.setPassword(passwordEncoder.encode("1234"));
        usuario.setNombre(mecanico.getNombre());
        usuario.setApellidos("MECANICO");
        usuario.setCorreo("mecanico@" + username + ".com");
        usuario.setTelefono("00000000");
        usuario.setActivo(true);

        usuarioRepository.save(usuario);

        mecanico.setUsuario(usuario);
        mecanicoRepository.save(mecanico);

    } else {
        // EDITAR
        Mecanico existente = mecanicoRepository.findById(mecanico.getIdMecanico())
                .orElseThrow(() -> new IllegalStateException("Mecánico no encontrado"));

        existente.setNombre(mecanico.getNombre());
        existente.setCedula(mecanico.getCedula());
        existente.setEspecialidad(mecanico.getEspecialidad());
        // opcional: actualizar datos de Usuario si quieres
        mecanicoRepository.save(existente);
    }
}





    // =========================
    // LISTAR / OBTENER
    // =========================
    public List<Mecanico> listarTodos() {
        return mecanicoRepository.findAll();
    }

    public Mecanico obtener(Integer id) {
        return mecanicoRepository.findById(id).orElseThrow();
    }

    // =========================
    // DISPONIBLES
    // =========================
    public List<Mecanico> listarDisponibles() {
        return mecanicoRepository.findAll().stream()
                .filter(m ->
                        citaRepository
                                .findByMecanicoIdMecanicoAndEstado(
                                        m.getIdMecanico(),
                                        Cita.Estado.EN_PROGRESO
                                ).isEmpty()
                )
                .collect(Collectors.toList());
    }

    // =========================
    // ELIMINAR CON VALIDACIÓN
    // =========================
    @Transactional
    public void eliminar(Integer id) {

        boolean tieneCitaActiva =
                !citaRepository
                        .findByMecanicoIdMecanicoAndEstado(
                                id,
                                Cita.Estado.EN_PROGRESO
                        ).isEmpty();

        if (tieneCitaActiva) {
            throw new IllegalStateException(
                    "No se puede eliminar un mecánico con citas activas"
            );
        }

        mecanicoRepository.deleteById(id);
    }
}
