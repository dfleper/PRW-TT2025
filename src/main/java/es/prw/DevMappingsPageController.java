package es.prw;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

@Profile("dev")
@Controller
public class DevMappingsPageController {

	private final RequestMappingHandlerMapping handlerMapping;

	public DevMappingsPageController(
			@Qualifier("requestMappingHandlerMapping") RequestMappingHandlerMapping handlerMapping) {
		this.handlerMapping = handlerMapping;
	}

	@GetMapping(value = "/dev/mappings", produces = "text/html; charset=UTF-8")
	@ResponseBody
	public String mappingsHtml() {

		String rows = handlerMapping.getHandlerMethods().entrySet().stream().map(e -> {
			var info = e.getKey();
			var method = e.getValue();

			String patterns = info.getPatternValues().isEmpty() ? "-" : String.join(", ", info.getPatternValues());

			String httpMethods = info.getMethodsCondition().getMethods().isEmpty() ? "ANY"
					: info.getMethodsCondition().getMethods().toString();

			String handler = method.getBeanType().getSimpleName() + "#" + method.getMethod().getName();

			return "<tr><td>" + httpMethods + "</td><td>" + patterns + "</td><td>" + handler + "</td></tr>";
		}).sorted().collect(Collectors.joining("\n"));

		return """
				<!doctype html>
				<html lang="es">
				<head>
				  <meta charset="utf-8"/>
				  <title>TT2025 - Mappings (DEV)</title>
				  <style>
				    body{font-family:system-ui,Segoe UI,Arial;margin:20px}
				    input{width:420px;padding:8px;margin:10px 0}
				    table{border-collapse:collapse;width:100%}
				    th,td{border:1px solid #ddd;padding:8px;vertical-align:top}
				    th{background:#f5f5f5;text-align:left}
				    tr:hover{background:#fafafa}
				    .muted{color:#666}
				  </style>
				</head>
				<body>
				  <h2>Mappings registrados (DEV)</h2>
				  <div class="muted">Incluye @Controller (Thymeleaf) y @RestController</div>
				  <input id="q" placeholder="Filtrar por ruta o handler (ej: /cliente, Appointment, api)"/>
				  <table id="t">
				    <thead><tr><th>Método</th><th>Ruta</th><th>Handler</th></tr></thead>
				    <tbody>
				""" + rows + """
				    </tbody>
				  </table>
				  <script>
				    const q=document.getElementById('q');
				    const tbody=document.querySelector('#t tbody');
				    q.addEventListener('input', () => {
				      const v=q.value.toLowerCase();
				      [...tbody.querySelectorAll('tr')].forEach(tr=>{
				        tr.style.display = tr.textContent.toLowerCase().includes(v) ? '' : 'none';
				      });
				    });
				  </script>
				</body>
				</html>
				""";
	}
}
