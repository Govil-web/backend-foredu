-- Insertar en Institucion

INSERT INTO institucion (nombre, direccion, telefono, email, logo, identificacion, nivel_educativo)
VALUES ('ForEdu', 'Latam', '1254785', 'foredu_educacion@gmail.com', 'www.foredulogo.com','20-884588-65', 'PRIMARIA');

-- Insertar usuarios
INSERT INTO users (dni, tipo_documento, activo, nombre, apellido, contraseña, email, telefono, rol, user_type, institucion_id )
VALUES ('12345678A', 'DNI', 1, 'Juan', 'Pérez', '$2a$10$I0gnxgl3zHLeuDQWurpwce8/rvcwOAjCM0kOlVazBv9WVbrReovq6', 'profesor@gmail.com', '123456789', 'ROLE_PROFESOR', 'PROFESOR', 1),
       ('12345678D', 'DNI', 1, 'Juan2', 'Pérez2', '$2a$10$I0gnxgl3zHLeuDQWurpwce8/rvcwOAjCM0kOlVazBv9WVbrReovq6', 'profesor2@gmail.com', '123456789', 'ROLE_PROFESOR', 'PROFESOR', 1),
       ('12345678E', 'DNI', 1, 'Juan3', 'Pérez3', '$2a$10$I0gnxgl3zHLeuDQWurpwce8/rvcwOAjCM0kOlVazBv9WVbrReovq6', 'profesor3@gmail.com', '123456789', 'ROLE_PROFESOR', 'PROFESOR', 1),
       ('23456789B', 'DNI', 1, 'Ana', 'Gómez', '$2a$10$I0gnxgl3zHLeuDQWurpwce8/rvcwOAjCM0kOlVazBv9WVbrReovq6', 'tutor@gmail.com', '987654321', 'ROLE_TUTOR', 'TUTOR_LEGAL', 1),
       ('34567890C', 'DNI', 1, 'Luis', 'Martínez', '$2a$10$I0gnxgl3zHLeuDQWurpwce8/rvcwOAjCM0kOlVazBv9WVbrReovq6', 'admin@gmail.com', '456789123', 'ROLE_ADMINISTRADOR', 'User', 1);

-- Insertar profesores
INSERT INTO profesores (user_id, materia)
VALUES (1, 'MATEMATICAS'),
       (2, 'LENGUAJE'),
       (3, 'CIENCIAS');

-- Insertar tutores legales
INSERT INTO tutor_legal (user_id)
VALUES (4);

-- Insertar administradores escolares
INSERT INTO administrador_escolar (user_id)
VALUES (5);
-- Insertar grados
INSERT INTO grado (aula, curso, turno, materia,contador, profesor_id)
VALUES ('A', 'PRIMERO', 'MAÑANA', 'MATEMATICAS',2, 1),
       ('B', 'SEGUNDO', 'TARDE', 'LENGUAJE',2, 2),
       ('C', 'TERCERO', 'NOCHE', 'CIENCIAS',2, 3);


-- Insertar estudiantes
INSERT INTO estudiantes (nombre, apellido, dni, genero, rol, fecha_nacimiento, activo, tipo_documento, grado_id ,tutor_legal_id)
VALUES ('Carlos', 'Sánchez', '45678901D', 'MASCULINO', 'ROLE_ESTUDIANTE', '2010-04-15', 1,  'DNI',1 ,4),
       ('Carlos2', 'Sánchez2', '45678901F', 'MASCULINO', 'ROLE_ESTUDIANTE', '2010-04-15', 1,  'DNI',1 ,4),
       ('Mario', 'Rodriguez', '751558', 'MASCULINO', 'ROLE_ESTUDIANTE', '2012-12-20', 1,  'DNI',3 ,4);

-- Insertar relación entre profesores y estudiantes
INSERT INTO profesor_estudiante (profesor_id, estudiante_id, materia)
VALUES (1, 1, 'MATEMATICAS'),
       (2, 1,'LENGUAJE'),
       (3, 1,'CIENCIAS');

-- Insertar notificaciones
INSERT INTO notificaciones (administrador_escolar_id, fecha_envio, tutor_legal_id, mensaje, tipo_notificacion, titulo)
VALUES (5, NOW(), 4, 'Reunión de seguimiento', 'EVENTO', 'Reunión de seguimiento para Carlos');

-- Insertar tareas
INSERT INTO tarea (profesor_id, estudiante_id, titulo, descripcion, estado_de_entrega, fecha_entrega, activo)
VALUES (1, 1, 'Tarea de Matemáticas', 'Resolver problemas del capítulo 3', 'PENDIENTE', '2024-09-30', 1),
       (2, 1, 'Tarea de lenguaje', 'Resolver cuestionario del capítulo 4', 'PENDIENTE', '2024-09-30', 1);

-- Insertar boletines
INSERT INTO boletin (estudiante_id, profesor_id, periodo, promedio, fecha, curso, observaciones)
VALUES (1, 1, 'Primer trimestre', 8.5, NOW(), 'PRIMERO', 'Buen desempeño');

-- Insertar calificaciones
INSERT INTO calificaciones (estudiante_id, profesor_id, boletin_id, materia, nota, promedio, comentario, fecha, periodo)
VALUES (1, 1, 1, 'MATEMATICAS', 9.0, 8.5, 'Excelente', NOW(), 'Primer trimestre'),
       (1, 2, 1, 'LENGUAJE', 9.0, 8.5, 'Excelente', NOW(), 'Primer trimestre');
       
 -- Insertar FECHA      
INSERT INTO fecha (anio, dia, mes, trimestre, semana, fecha) VALUES
(2025, 16, 2, 1, 3, '2025-02-16'),
(2025, 17, 2, 1, 3, '2025-02-17'),
(2025, 18, 2, 1, 3, '2025-02-18');

-- Insertar asistencia

INSERT INTO asistencia (observaciones, estado, estudiante_id, fecha_id, grado_id) VALUES
('Llegó a tiempo', 'PRESENTE', 1, 1, 1),
('Faltó sin justificación', 'AUSENTE', 1, 2, 1),

('Tarde por tráfico', 'PRESENTE', 2, 1, 1),
('Faltó por enfermedad', 'JUSTIFICADO', 2, 2, 1),

('Presente en clase', 'PRESENTE', 3, 1, 1),
('Ausente sin aviso', 'PRESENTE', 3, 2, 1);

