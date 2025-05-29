package com.codewithmosh.store.controllers;

import com.codewithmosh.store.dtos.ChangePasswordRequest;
import com.codewithmosh.store.dtos.RegisterUserRequest;
import com.codewithmosh.store.dtos.UpdateUserRequest;
import com.codewithmosh.store.dtos.UserDto;
import com.codewithmosh.store.mappers.UserMapper;
import com.codewithmosh.store.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Set;


@RestController
@AllArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserRepository userRepository;
    private final UserMapper userMapper;


    @GetMapping
    public Iterable<UserDto> getUsers(
            @RequestHeader(name = "x-auth-token", required = false) String authToken,
            @RequestParam(required = false, defaultValue = "", name = "sort") String sortby) {
        System.out.println(authToken);
        if (!Set.of("name", "email").contains(sortby))
            sortby = "name";
        return userRepository.findAll(Sort.by(sortby).ascending())
                .stream()
                .map(userMapper::userToUserDto)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        var user = userRepository.findById(id).orElse(null);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(userMapper.userToUserDto(user));
    }

    @PostMapping
    public ResponseEntity<UserDto> createUser(
            @RequestBody RegisterUserRequest request,
            UriComponentsBuilder uriBuilder
            ) {
        var user = userMapper.toEntity(request);
        userRepository.save(user);
        var userDto=userMapper.userToUserDto(user);
        var uri =uriBuilder.path("/users/{id}").build(userDto.getId());
        return ResponseEntity.created(uri).body(userDto);

    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(
            @PathVariable(name = "id") Long id,
            @RequestBody UpdateUserRequest request)
    {
      var user = userRepository.findById(id).orElse(null);
      if (user == null) {
          return ResponseEntity.notFound().build();
      }
      userMapper.updateUser(request, user);
      userRepository.save(user);
      var userDto=userMapper.userToUserDto(user);
      return ResponseEntity.ok(userDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id){
        var user = userRepository.findById(id).orElse(null);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        userRepository.delete(user);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("{id}/change-password")
    public  ResponseEntity<Void> changePassword(@PathVariable Long id, @RequestBody ChangePasswordRequest request) {
        var user =userRepository.findById(id).orElse(null);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        if(!user.getPassword().equals(request.getOldPassword())){
           return  new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        user.setPassword(request.getNewPassword());
        userRepository.save(user);
        return ResponseEntity.noContent().build();
    }
}
