package car.adverts.carsApp.Controller;


import car.adverts.carsApp.Models.Car;
import car.adverts.carsApp.Repo.CarRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ApiController {

    @Autowired
    private CarRepo carRepo;

    @GetMapping(value = "/car/adverts")
    public ResponseEntity<?> getCars(@RequestParam(required = false) String sortby) {
        List<Car> res;
        if (sortby != null && sortby.equals("price")) {
            res = carRepo.findAll(Sort.by("price"));
        } else {
            res = carRepo.findAll();
        }
        if (!res.isEmpty()) {
            return new ResponseEntity<List<Car>>(res, HttpStatus.OK);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error Response:\n" +
                    "Code: 404 (Not found)\n" +
                    "Explanation: No car advert list was found.");
        }
    }

    //// Get Car By ID
    @GetMapping(value = "/car/adverts/{id}")
    public ResponseEntity<?> getCarById(@PathVariable long id) {
        Car res;
        try {
            res = carRepo.findById(id).get();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error Response:\n" +
                    "Code: 404 (Not found)\n" +
                    "Explanation: No car advert with given id was found.");
        }

        return new ResponseEntity<Car>(res, HttpStatus.OK);

    }

    @PostMapping(value = "/save")
    public ResponseEntity<?> saveCar(@RequestBody Car car) {
        // checking json validity
        if(car.getTitle()==null || car.getFirstRegistration()==null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error Response:\n" +
                    "Code: 400 (Bad request)\n" +
                    "Explanation: This is returned if json is invalid or cannot be parsed.");
        }
        // Check is price negative number
        if(car.getPrice() > 0){
            carRepo.save(car);
            return new ResponseEntity<>(car,HttpStatus.CREATED);
        }else{
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body("Error Response:\n" +
                    "Code: 422 (Unprocessable entity)\n" +
                    "Explanation: Validation failed.");
        }

    }

    @PutMapping(value = "update/{id}")
    public ResponseEntity<?> updateCar(@PathVariable long id, @RequestBody Car car) {
        Car updatedCar;
        try {
            updatedCar = carRepo.findById(id).get();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error Response:\n" +
                    "Code: 404 (Not found)\n" +
                    "Explanation: No car advert with given id was found.");
        }
        updatedCar.setTitle(car.getTitle());
        updatedCar.setFuelType(car.getFuelType());
        updatedCar.setPrice(car.getPrice());
        updatedCar.setNew(car.isNew());
        updatedCar.setMileage(car.getMileage());
        updatedCar.setFirstRegistration(car.getFirstRegistration());
        return saveCar(updatedCar);
    }

    @DeleteMapping(value = "/delete/{id}")
    public ResponseEntity deleteCar(@PathVariable long id) {
        Car deleteCar;
        try {
            deleteCar = carRepo.findById(id).get();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error Response:\n" +
                    "Code: 404 (Not found)\n" +
                    "Explanation: This is returned if a car advert with given id is not found..");
        }

        carRepo.delete(deleteCar);
        try {
            deleteCar = carRepo.findById(id).get();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Error Response:\n" +
                    "Code: 204 (No content)\n" );
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error Response:\n" +
                "Code: 400 (Bad Request)\n" +
                "Explanation: Unable to delete");
    }


}
