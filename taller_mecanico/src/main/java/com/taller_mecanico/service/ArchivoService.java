/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.taller_mecanico.service;

import com.taller_mecanico.domain.Archivo;
import com.taller_mecanico.repository.ArchivoRepository;
import com.taller_mecanico.repository.CitaRepository;
import com.taller_mecanico.repository.VehiculoRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author megan
 */
@Service
public class ArchivoService {

    private final ArchivoRepository archivoRepo;
    private final CitaRepository citaRepo;
    private final VehiculoRepository vehiculoRepo;
    private final FirebaseStorageService firebase;

    public ArchivoService(
            ArchivoRepository archivoRepo,
            CitaRepository citaRepo,
            VehiculoRepository vehiculoRepo,
            FirebaseStorageService firebase
    ) {
        this.archivoRepo = archivoRepo;
        this.citaRepo = citaRepo;
        this.vehiculoRepo = vehiculoRepo;
        this.firebase = firebase;
    }

    public Archivo subirEvidenciaDeCita(Integer idCita, MultipartFile file) {
        try {
            String url = firebase.uploadImage(file, "citas/" + idCita, idCita);
            var cita = citaRepo.findById(idCita).orElseThrow();

            var a = new Archivo();
            a.setCita(cita);
            a.setTipo(Archivo.Tipo.EVIDENCIA_SERVICIO);
            a.setUrlPublica(url);
            a.setNombreArchivo(file.getOriginalFilename());
            return archivoRepo.save(a);
        } catch (Exception e) {
            throw new RuntimeException("No se pudo subir la evidencia: " + e.getMessage(), e);
        }
    }

    public Archivo subirFotoDeVehiculo(Integer idVehiculo, MultipartFile file) {
        try {
            String url = firebase.uploadImage(file, "vehiculos/" + idVehiculo, idVehiculo);
            var vehiculo = vehiculoRepo.findById(idVehiculo).orElseThrow();

            var a = new Archivo();
            a.setVehiculo(vehiculo);
            a.setTipo(Archivo.Tipo.FOTO_VEHICULO);
            a.setUrlPublica(url);
            a.setNombreArchivo(file.getOriginalFilename());
            return archivoRepo.save(a);
        } catch (Exception e) {
            throw new RuntimeException("No se pudo subir la foto del vehículo: " + e.getMessage(), e);
        }
    }
}
