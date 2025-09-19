package school.sptech.prova_ac1;

import oracle.jdbc.proxy.annotation.Post;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {
    private List<Usuario> usuarios = new ArrayList<>();

    @GetMapping
    public ResponseEntity<List<Usuario>> buscarTodos() {
        if (usuarios.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(usuarios);
        }
    }

    @PostMapping
    public ResponseEntity<Usuario> criar(Usuario usuario) {
        usuarios.add(usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(usuario);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> buscarPorId(@PathVariable Integer id) {
        Usuario usuarioPorId = usuarios.stream()
                .filter(user -> user.getId().equals(id))
                .findFirst()
                .orElse(null);

        if (usuarioPorId == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(usuarioPorId);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Integer id) {
        Usuario usuarioDeletado = usuarios.stream()
                .filter(user -> user.getId().equals(id))
                .findFirst()
                .orElse(null);

        if (usuarioDeletado == null) {
            return ResponseEntity.notFound().build();
        }
        usuarios.remove(usuarioDeletado);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/filtro-data")
    public ResponseEntity<List<Usuario>> buscarPorDataNascimento(@PathVariable LocalDate nascimento) {
        List<Usuario> filtrados = usuarios.stream()
                .filter(user -> user.getDataNascimento().isAfter(nascimento)).toList();

        if (filtrados.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(filtrados);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Usuario> atualizar(@PathVariable Integer id, @RequestBody Usuario usuarioAtualizado) {
        Usuario usuario = usuarios.stream()
                .filter(user -> user.getId().equals(id))
                .findFirst()
                .orElse(null);

        if (usuario == null) {
            return ResponseEntity.notFound().build();
        }

        Boolean cpfExiste = usuarios.stream().anyMatch(user -> !user.getId().equals(id) && user.getCpf().equals(usuarioAtualizado.getCpf()));
        Boolean emailExiste = usuarios.stream().anyMatch(user -> !user.getId().equals(id) && user.getEmail().equals(usuarioAtualizado.getEmail()));

        if (cpfExiste || emailExiste) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        usuario.setNome(usuarioAtualizado.getNome());
        usuario.setCpf(usuarioAtualizado.getCpf());
        usuario.setEmail(usuarioAtualizado.getEmail());
        usuario.setSenha(usuarioAtualizado.getSenha());
        usuario.setDataNascimento(usuarioAtualizado.getDataNascimento());

        return ResponseEntity.ok(usuario);
    }

}
