// Script de inicializacion de MongoDB - Sistema de Gestion Academica
// Uso:  mongosh scripts/init_db.js
// ATENCION: elimina y recrea las colecciones de la base "gestion_academica".

const bd = db.getSiblingDB("gestion_academica");

["usuarios", "estudiantes", "cursos", "matriculas"].forEach(c => bd.getCollection(c).drop());
["usuarios", "estudiantes", "cursos", "matriculas"].forEach(c => bd.createCollection(c));

// Indices unicos (garantizan codigos no duplicados)
bd.usuarios.createIndex({ username: 1 }, { unique: true });
bd.estudiantes.createIndex({ codigo: 1 }, { unique: true });
bd.estudiantes.createIndex({ documento: 1 }, { unique: true });
bd.cursos.createIndex({ codigo: 1 }, { unique: true });
bd.matriculas.createIndex({ codigo: 1 }, { unique: true });

// Usuario de prueba: admin / Admin123*  (se guarda solo el hash PBKDF2, nunca la clave en texto plano)
bd.usuarios.insertOne({
  username: "admin",
  passwordHash: "120000$obLD1OX2BxgpOktcbX6PkA==$1pq69/Ir9P90eViiyoEHqBAnVaHBC+cpn5kkBryGBTk=",
  rol: "ADMIN"
});

bd.estudiantes.insertMany([
  { codigo: "E001", nombre: "Ana María López", documento: "01234567-8", correo: "ana.lopez@correo.com", activo: true },
  { codigo: "E002", nombre: "Carlos Pérez", documento: "02345678-9", correo: "carlos.perez@correo.com", activo: true },
  { codigo: "E003", nombre: "Lucía Martínez", documento: "03456789-0", correo: "lucia.martinez@correo.com", activo: true },
  { codigo: "E004", nombre: "José Hernández", documento: "04567890-1", correo: "", activo: true },
  { codigo: "E005", nombre: "Sofía Ramírez", documento: "05678901-2", correo: "sofia.ramirez@correo.com", activo: true },
  { codigo: "E006", nombre: "Miguel Torres", documento: "06789012-3", correo: "miguel.torres@correo.com", activo: false }
]);

bd.cursos.insertMany([
  { codigo: "C001", nombre: "Programación Orientada a Objetos", descripcion: "Fundamentos de POO con Java", docente: "Ing. Rivas", cupoMaximo: 3 },
  { codigo: "C002", nombre: "Bases de Datos", descripcion: "Modelado y consultas", docente: "Ing. Castillo", cupoMaximo: 30 },
  { codigo: "C003", nombre: "Matemática I", descripcion: "Cálculo diferencial", docente: "Lic. Mejía", cupoMaximo: 25 },
  { codigo: "C004", nombre: "Estructuras de Datos", descripcion: "Listas, pilas, colas y árboles", docente: "Ing. Rivas", cupoMaximo: 20 },
  { codigo: "C005", nombre: "Inglés Técnico", descripcion: "Lectura de documentación", docente: "Lic. Flores", cupoMaximo: 15 },
  { codigo: "C006", nombre: "Redes de Computadoras", descripcion: "Modelo OSI y TCP/IP", docente: "Ing. Molina", cupoMaximo: 20 }
]);

// C001 queda lleno (cupo 3) para poder probar la validacion de cupos
bd.matriculas.insertMany([
  { codigo: "MAT-0001", codigoEstudiante: "E001", codigoCurso: "C001", fecha: "2026-10-20", estado: "ACTIVA", fechaRetiro: null },
  { codigo: "MAT-0002", codigoEstudiante: "E002", codigoCurso: "C001", fecha: "2026-10-20", estado: "ACTIVA", fechaRetiro: null },
  { codigo: "MAT-0003", codigoEstudiante: "E003", codigoCurso: "C001", fecha: "2026-10-21", estado: "ACTIVA", fechaRetiro: null },
  { codigo: "MAT-0004", codigoEstudiante: "E001", codigoCurso: "C002", fecha: "2026-10-22", estado: "ACTIVA", fechaRetiro: null },
  { codigo: "MAT-0005", codigoEstudiante: "E004", codigoCurso: "C002", fecha: "2026-10-22", estado: "ACTIVA", fechaRetiro: null },
  { codigo: "MAT-0006", codigoEstudiante: "E005", codigoCurso: "C003", fecha: "2026-10-23", estado: "RETIRADA", fechaRetiro: "2026-10-30" },
  { codigo: "MAT-0007", codigoEstudiante: "E002", codigoCurso: "C004", fecha: "2026-10-24", estado: "ACTIVA", fechaRetiro: null }
]);

print("Base de datos 'gestion_academica' inicializada. Usuario de prueba: admin / Admin123*");
