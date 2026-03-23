package cat.copernic.backendProjecte3;

import cat.copernic.backendProjecte3.config.PasswordHasher;
import cat.copernic.backendProjecte3.entities.Client;
import cat.copernic.backendProjecte3.entities.Reserva;
import cat.copernic.backendProjecte3.entities.Vehicle;
import cat.copernic.backendProjecte3.enums.Reputacio;
import cat.copernic.backendProjecte3.enums.TipusVehicle;
import cat.copernic.backendProjecte3.enums.UserRole;
import cat.copernic.backendProjecte3.repository.ClientRepository;
import cat.copernic.backendProjecte3.repository.ReservaRepository;
import cat.copernic.backendProjecte3.repository.VehicleRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.ClassPathResource;

@SpringBootApplication
public class BackendProjecte3Application implements CommandLineRunner {

    @Autowired
    private VehicleRepository vehicleRepo;

    @Autowired
    private ClientRepository clientRepo;

    @Autowired
    private ReservaRepository reservaRepo;

    public static void main(String[] args) {
        SpringApplication.run(BackendProjecte3Application.class, args);
    }

    @Override
    public void run(String... args) {

        System.out.println("Inserint dades de prova...");

        try {

            // CLIENTS
            Client c1 = crearClient(
                    "Maria Garcia López",
                    "maria@test.com",
                    "44556677D",
                    "España",
                    "15/08/2030",
                    "22/11/2029",
                    "Carrer Major 12, 1r 2a, Barcelona",
                    "B",
                    "4532756279624064",
                    "/images/client/profile1.png",
                    "/images/client/dni.jpg",
                    "/images/client/carnet.jpg"
            );

            Client c2 = crearClient(
                    "Joan Martínez Ruiz",
                    "joan@test.com",
                    "12345678A",
                    "Perú",
                    "02/04/2029",
                    "10/01/2028",
                    "Avinguda Catalunya 45, Sabadell",
                    "B",
                    "4716461583322103",
                    "/images/client/profile2.png",
                    "/images/client/dni.jpg",
                    "/images/client/carnet.jpg"
            );

            Client c3 = crearClient(
                    "Laura Fernández Costa",
                    "laura@test.com",
                    "87654321B",
                    "Italia",
                    "30/09/2031",
                    "18/06/2030",
                    "Passeig de Gràcia 101, Barcelona",
                    "B",
                    "4485275742308327",
                    "/images/client/profile3.jpg",
                    "/images/client/dni.jpg",
                    "/images/client/carnet.jpg"
            );

            Client c4 = crearClient(
                    "Darren Escosio Leones",
                    "darren@test.com",
                    "11223344C",
                    "España",
                    "05/12/2032",
                    "14/03/2031",
                    "Carrer de la Marina 88, Barcelona",
                    "B",
                    "4556737586899855",
                    "/images/client/profile4.png",
                    "/images/client/dni.jpg",
                    "/images/client/carnet.jpg"
            );

            // VEHICLES
            Vehicle v1 = crearVehicle(
                    "9988GTI",
                    TipusVehicle.COTXE,
                    "Elèctric",
                    new BigDecimal("10.00"),
                    new BigDecimal("250.00"),
                    "/images/vehicles/golf-gti-8-gris-1024x768.jpeg",
                    "Blau",
                    "130",
                    450,
                    1,
                    10,
                    "Vehicle en molt bon estat. Revisió passada aquest mes."
            );

            Vehicle v2 = crearVehicle(
                    "1234BMW",
                    TipusVehicle.COTXE,
                    "Gasolina",
                    new BigDecimal("20.00"),
                    new BigDecimal("300.00"),
                    "/images/vehicles/audi-r8-dinamismo-carretera-593531.jpg",
                    "Negre",
                    "100",
                    350,
                    2,
                    7,
                    "Vehicle esportiu. Cal revisar pneumàtics abans de trajectes llargs."
            );

            Vehicle v3 = crearVehicle(
                    "5678TES",
                    TipusVehicle.COTXE,
                    "Elèctric",
                    new BigDecimal("15.00"),
                    new BigDecimal("200.00"),
                    "/images/vehicles/tesla-model3.jpg",
                    "Blanc",
                    "200",
                    500,
                    1,
                    14,
                    "Autonomia alta. Compatible amb càrrega ràpida."
            );

            Vehicle v4 = crearVehicle(
                    "1792USA",
                    TipusVehicle.TANK,
                    "Gasolina",
                    new BigDecimal("750.00"),
                    new BigDecimal("7500.00"),
                    "/images/vehicles/carro_combate_m1_Abrams.jpg",
                    "Verd",
                    "1500",
                    1200,
                    7,
                    21,
                    null
            );

            // RESERVES
            // Maria tindrà 3 reserves
            crearReserva(c1, v1, 1);
            crearReserva(c1, v2, 3);
            crearReserva(c1, v4, 7);

            // Joan
            crearReserva(c2, v1, 2);
            crearReserva(c2, v3, 5);

            // Laura
            crearReserva(c3, v2, 4);
            crearReserva(c3, v3, 6);

            // Darren
            crearReserva(c4, v4, 8);
            crearReserva(c4, v1, 9);

            System.out.println("Dades inserides correctament.");

        } catch (Exception e) {
            System.err.println("ERROR inserint dades: " + e.getMessage());
        }
    }

    private Client crearClient(
            String nomComplet,
            String email,
            String dni,
            String nacionalitat,
            String dataCaducitatDocument,
            String dataCaducitatCarnetConduir,
            String adreca,
            String carnetConduir,
            String numeroTargetaCredit,
            String fotoPerfil,
            String fotoDni,
            String fotoCarnet
    ) throws Exception {

        Client c = new Client();

        c.setNomComplet(nomComplet);
        c.setEmail(email);
        c.setPassword(PasswordHasher.encode("123456"));
        c.setDni(dni);
        c.setNacionalitat(nacionalitat);
        c.setDataCaducitatDocument(dataCaducitatDocument);
        c.setDataCaducitatCarnetConduir(dataCaducitatCarnetConduir);
        c.setAdreca(adreca);
        c.setCarnetConduir(carnetConduir);
        c.setNumeroTargetaCredit(numeroTargetaCredit);
        c.setReputacio(Reputacio.PREMIUM);
        c.setRol(UserRole.CLIENT);

        c.setFotoPerfil(new ClassPathResource("static" + fotoPerfil).getInputStream().readAllBytes());
        c.setDocIdentitat(new ClassPathResource("static" + fotoDni).getInputStream().readAllBytes());
        c.setDocCarnet(new ClassPathResource("static" + fotoCarnet).getInputStream().readAllBytes());

        return clientRepo.save(c);
    }

    private Vehicle crearVehicle(
            String matricula,
            TipusVehicle tipus,
            String motor,
            BigDecimal preu,
            BigDecimal fianca,
            String foto,
            String color,
            String potencia,
            Integer limitQuilometratge,
            Integer minDiesLloguer,
            Integer maxDiesLloguer,
            String comentarisPrivats
    ) {

        if (vehicleRepo.existsById(matricula)) {
            return vehicleRepo.findById(matricula).orElseThrow();
        }

        Vehicle v = new Vehicle();
        v.setMatricula(matricula);
        v.setTipusVehicle(tipus);
        v.setMotor(motor);
        v.setPreuHora(preu);
        v.setFiancaEstandard(fianca);
        v.setRutaDocumentacioPrivada(foto);
        v.setColor(color);
        v.setPotencia(potencia);
        v.setLimitQuilometratge(limitQuilometratge);
        v.setMinDiesLloguer(minDiesLloguer);
        v.setMaxDiesLloguer(maxDiesLloguer);
        v.setComentarisPrivats(comentarisPrivats);

        return vehicleRepo.save(v);
    }

    private void crearReserva(Client c, Vehicle v, int setmanaOffset) {

        Reserva r = new Reserva();
        r.setClient(c);
        r.setVehicle(v);

        LocalDate inici = LocalDate.now().plusWeeks(setmanaOffset);
        LocalDate fi = inici.plusDays(2);

        r.setDataInici(inici);
        r.setDataFi(fi);

        long dies = ChronoUnit.DAYS.between(inici, fi);
        BigDecimal importTotal = v.getPreuHora()
                .multiply(BigDecimal.valueOf(24))
                .multiply(BigDecimal.valueOf(dies));

        r.setImportTotal(importTotal);
        r.setFianca(v.getFiancaEstandard());

        reservaRepo.save(r);
    }
}