
-- Tabla con ciudadanos dados de alta para votar 

CREATE TABLE censo
(
numeroDNI char(9) not null,
nombre    varchar(128) not null,
fechaNacimiento char(8) not null,
anioCenso char(4) not null,
codigoAutorizacion char(3) not null,
PRIMARY KEY (numeroDNI)
);

INSERT INTO censo(numeroDNI,nombre,fechaNacimiento,anioCenso,codigoAutorizacion)
VALUES ('23424434Y','Jose Garcia Perez','23/11/82','2024','123');

-- Tabla con votos autorizados

CREATE TABLE voto
(
-- idVoto se autogenera con cada inserción
idVoto serial   not null,
idCircunscripcion char(16) not null,
idMesaElectoral char(16) not null,
idProcesoElectoral char(16) not null,
nombreCandidatoVotado char(16) not null,
codRespuesta   char(3)  not null default '000',
numeroDNI char(9) not null references censo,
marcaTiempo timestamp not null default current_timestamp,
--- restricción para evitar que una persona vote varias veces en un mismo proceso electoral, 
CONSTRAINT     Registro_UC  unique(idProcesoElectoral, numeroDNI),
PRIMARY KEY (idVoto)
);


