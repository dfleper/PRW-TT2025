-- Crea registros en employees para usuarios con rol MECANICO que todavía no tengan empleado asociado.
-- Completa explícitamente todos los campos relevantes para evitar filas parciales en el seed.
INSERT INTO employees (
  id_user,
  tipo,
  especialidad,
  fecha_alta,
  activo,
  created_at,
  updated_at,
  created_by_user,
  updated_by_user
)
SELECT
  u.id_user,
  'mecanico',
  'Mecánica general',
  CURDATE(),
  u.activo,
  NOW(),
  NOW(),
  admin.id_user,
  admin.id_user
FROM users u
JOIN user_roles ur ON ur.id_user = u.id_user
JOIN roles r ON r.id_role = ur.id_role
LEFT JOIN employees e ON e.id_user = u.id_user
LEFT JOIN users admin ON admin.email = 'admin@tt2025.local'
WHERE r.nombre = 'MECANICO'
  AND e.id_employee IS NULL;