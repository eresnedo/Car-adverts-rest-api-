package car.adverts.carsApp.Repo;
import car.adverts.carsApp.Models.Car;
import org.springframework.data.jpa.repository.JpaRepository;


public interface CarRepo extends JpaRepository<Car, Long> {
}
