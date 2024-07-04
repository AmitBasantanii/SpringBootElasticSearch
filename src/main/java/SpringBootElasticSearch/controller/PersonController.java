//package SpringBootElasticSearch.controller;
//
//import SpringBootElasticSearch.entity.Person;
//import SpringBootElasticSearch.service.PersonService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.*;
//
////@RestController
//@RequestMapping("/api/person")
//public class PersonController {
//    private final PersonService personService;
//
//    @Autowired
//    public PersonController (PersonService personService) {
//        this.personService = personService;
//    }
//
//    @PostMapping
//    public void save(@RequestBody final Person person) {
//        personService.save(person);
//    }
//
//    @GetMapping("/{id}")
//    public Person findById(@PathVariable final String id) {
//        return personService.findById(id);
//    }
//}
