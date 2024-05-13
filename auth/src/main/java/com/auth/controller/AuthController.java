package com.auth.controller;

import com.auth.dto.AuthenticationRequest;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import com.auth.dto.ChangePasswordDto;
import com.auth.dto.EntrepriseDto;
import com.auth.dto.RoleDto;
import com.auth.dto.SignupRequest;
import com.auth.dto.UserDto;
import com.auth.entity.Role;
import com.auth.entity.User;
import com.auth.exceptions.UserNotFoundException;
import com.auth.repository.UserRepository;
import com.auth.services.auth.AuthService;
import com.auth.services.auth.EntrepriseService;
import com.auth.services.auth.RoleService;
import com.auth.services.jwt.UserDetailsServiceImpl;
import com.auth.utils.JwtUtil;
import org.springframework.security.core.userdetails.UserDetails; // Import UserDetails
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.HttpHeaders;

import org.springframework.format.annotation.DateTimeFormat;
import java.io.IOException;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
	@Autowired
    private EntrepriseService entrepriseService;
	  private final AuthenticationManager authenticationManager;
	  @Autowired
	    private final AuthService authService;
	  @Autowired
	    private final UserDetailsServiceImpl userDetailsService; // Inject UserDetailsService
	  @Autowired
	    private final JwtUtil jwtUtil;
	  @Autowired
	    private RoleService roleService;
	  @Autowired
	  private UserRepository userRepository;
	  @PostMapping("/login")
	  public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest, HttpServletResponse response) {
	      try {
	          authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authenticationRequest.getEmail(), authenticationRequest.getPassword()));
	      } catch (BadCredentialsException e) {
	          return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Incorrect username or password");
	      }

	      final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getEmail());
	      Optional<User> optionalUser = userRepository.findFirstByEmail(authenticationRequest.getEmail());
	      if (optionalUser.isPresent()) {
	          User user = optionalUser.get();
	          if (authService.checkIfPasswordNeedsUpdate(user)) {
	              return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Password update required");
	          }

	          List<String> roles = user.getRoles().stream().map(Role::getName).collect(Collectors.toList());
	          final String jwt = jwtUtil.generateToken(user.getEmail(), roles);

	          // Set JWT to the response header
	          response.setHeader("Authorization", "Bearer " + jwt);

	          try {
	              // Convert byte array to Base64 string, handle null image
	        	  String base64Image = null;
	        	  if (user.getImg() != null) {
	        	      base64Image = Base64.getEncoder().encodeToString(user.getImg());
	        	      System.out.println("Base64 Image: " + base64Image); // Log the Base64 string
	        	  } else {
	        	      System.out.println("User image is null");
	        	  }
JSONObject jsonResponse = new JSONObject();
	              jsonResponse.put("jwt", jwt); 
	              jsonResponse.put("userId", user.getId());
	              jsonResponse.put("roles", roles);
	              jsonResponse.put("userImage", base64Image); // Send Base64 string

	              return ResponseEntity.ok().headers(new HttpHeaders()).body(jsonResponse.toString());
	          } catch (JSONException e) {
	              return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating JSON response");
	          }
	      } else {
	          return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
	      }
	  }





	  @PostMapping(value = "/signup", consumes = "multipart/form-data")
	  public ResponseEntity<?> signupUser(
	          @RequestParam("email") String email,
	          @RequestParam("name") String name,
	          @RequestParam("password") String password,
	          @RequestParam("cin") String cin,
	          @RequestParam("datenais") @DateTimeFormat(pattern = "yyyy-MM-dd") Date datenais,
	          @RequestParam("lieunais") String lieunais,
	          @RequestParam("img") MultipartFile img) {
	      try {
	          SignupRequest signupRequest = new SignupRequest();
	          signupRequest.setEmail(email);
	          signupRequest.setName(name);
	          signupRequest.setPassword(password);
	          signupRequest.setCin(cin);
	          signupRequest.setDatenais(datenais);
	          signupRequest.setLieunais(lieunais);
	          signupRequest.setImg(img.getBytes()); // Convert MultipartFile to byte[]

	          UserDto userDto = authService.createUser(signupRequest);

	          return ResponseEntity.ok(userDto);
	      } catch (IOException e) {
	          return ResponseEntity.status(500).body("Error processing image");
	      } catch (Exception e) {
	          return ResponseEntity.status(500).body("Error creating user");
	      }
	  }

   
	  @DeleteMapping("/delete/{id}")
	    public ResponseEntity<?> deleteUser(@PathVariable UUID id) {
	        try {
	            boolean deleted = authService.deleteUserById(id);
	            if (!deleted) {
	                return ResponseEntity.badRequest().body("User could not be deleted.");
	            }
	            return ResponseEntity.ok().body("User deleted successfully.");
	        } catch (UserNotFoundException e) {
	            return ResponseEntity.notFound().build();
	        }
	    }
	    @GetMapping("/roles")
	    public ResponseEntity<List<RoleDto>> getAllRoles() {
	        List<RoleDto> roles = roleService.getAllRoles();
	        return ResponseEntity.ok(roles);
	    }
	    @GetMapping("/user/{id}")
	    public ResponseEntity<UserDto> getUserById(@PathVariable UUID id) {
	        UserDto userDto = authService.getUserById(id);
	        return ResponseEntity.ok(userDto);
	    }

	    @PostMapping("/make-admin/{id}")
	    public ResponseEntity<?> makeAdmin(@PathVariable UUID id) {
	        UserDto userDto = authService.makeAdmin(id);
	        return userDto != null ? ResponseEntity.ok(userDto) : ResponseEntity.notFound().build();
	    }
	   
	    @PostMapping("/make-user/{id}")
	    public ResponseEntity<?> makeUser(@PathVariable UUID id) {
	        UserDto userDto = authService.makeUser(id);
	        return userDto != null ? ResponseEntity.ok(userDto) : ResponseEntity.notFound().build();
	    }

	    @GetMapping("/users")
	    public ResponseEntity<List<UserDto>> getAllUsers() {
	        List<UserDto> users = authService.getAllUsers();
	        return ResponseEntity.ok(users);
	    }
	    @GetMapping("/users/{name}")
	    public ResponseEntity<List<UserDto>> getUsersByName(@PathVariable String name) {
	        List<UserDto> users = authService.searchUsersByName(name);
	        return ResponseEntity.ok(users);
	    }
	    @PutMapping(value = "/updateuser/{id}", consumes = "multipart/form-data")
	    public ResponseEntity<?> updateUserById(
	            @PathVariable UUID id,
	            @RequestParam("email") String email,
	            @RequestParam("name") String name,
	            @RequestParam("cin") String cin,
	            @RequestParam("datenais") @DateTimeFormat(pattern = "yyyy-MM-dd") Date datenais,
	            @RequestParam("lieunais") String lieunais,
	            @RequestParam("img") MultipartFile img) {
	        try {
	            UserDto userDto = new UserDto();
	            userDto.setId(id);
	            userDto.setEmail(email);
	            userDto.setName(name);
	            userDto.setCin(cin);
	            userDto.setDatenais(datenais);
	            userDto.setLieunais(lieunais);
	            userDto.setImg(img.getBytes()); // Convert MultipartFile to byte[]
	            String base64Image = null;
	            if (userDto.getImg() != null) {
	                base64Image = Base64.getEncoder().encodeToString(userDto.getImg());
	            }
	            ResponseEntity<?> updatedUser = authService.updateUserById(userDto);

	            JSONObject jsonResponse = new JSONObject();
	            jsonResponse.put("userId", userDto.getId());
	            jsonResponse.put("email", userDto.getEmail());
	            jsonResponse.put("name", userDto.getName());
	            jsonResponse.put("cin", userDto.getCin());
	            jsonResponse.put("datenais", userDto.getDatenais());
	            jsonResponse.put("lieunais", userDto.getLieunais());
	            jsonResponse.put("userImage", base64Image); // Send Base64 string

	            return ResponseEntity.ok(updatedUser);
	        } catch (IOException e) {
	            return ResponseEntity.status(500).body("Error processing image");
	        } catch (Exception e) {
	            return ResponseEntity.status(500).body("Error updating user");
	        }
	    }
	    @PostMapping("/updatepassword")
	    public ResponseEntity<?> updatePassword(@RequestBody ChangePasswordDto changePasswordDto) {
	        try {
	            return authService.updatePasswordById(changePasswordDto);
	        } catch (Exception ex) {
	            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Something went wrong");
	        }
	    }
	    @PostMapping(value = "/user/{userId}/add-entreprise", consumes = "multipart/form-data", produces = "application/json")
	    public ResponseEntity<Map<String, String>> addEntrepriseToUser(
	            @PathVariable UUID userId,
	            @RequestParam("name") String name,
	            @RequestParam("adresse") String adresse,
	            @RequestParam("secteuractivite") String secteuractivite,
	            @RequestParam("Matricule") String Matricule,
	            @RequestParam("ville") String ville,
	            @RequestParam("siegesociale") String siegesociale,
	            @RequestParam("codeTVA") String codeTVA,
	            @RequestParam("logo") MultipartFile logo) {

	        Map<String, String> response = new HashMap<>();
	        try {
	            // Create an instance of EntrepriseDto and set its fields
	            EntrepriseDto entrepriseDto = new EntrepriseDto();
	            entrepriseDto.setName(name);
	            entrepriseDto.setAdresse(adresse);
	            entrepriseDto.setSecteuractivite(secteuractivite);
	            entrepriseDto.setMatricule(Matricule);
	            entrepriseDto.setVille(ville);
	            entrepriseDto.setSiegesociale(siegesociale);
	            entrepriseDto.setCodeTVA(codeTVA);

	            if (logo != null && !logo.isEmpty()) {
	                try {
	                    byte[] returnedImg = logo.getBytes(); // Converts the logo to a byte array
	                    entrepriseDto.setReturnedImg(returnedImg);
	                } catch (IOException e) {
	                    response.put("error", "Error reading logo file: " + e.getMessage());
	                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
	                }
	            }

	            authService.addEntrepriseToUser(userId, entrepriseDto); // assuming this is the method name in AuthService
	            response.put("message", "Entreprise added successfully");
	            return ResponseEntity.ok(response);
	        } catch (Exception e) {
	            response.put("error", "Error adding entreprise: " + e.getMessage());
	            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
	        }
	    }
	    @DeleteMapping("/entreprise/{id}")
	    public ResponseEntity<String> deleteEntreprise(@PathVariable UUID id) {
	        try {
	            entrepriseService.deleteEntreprise(id);
	            return ResponseEntity.ok("Entreprise deleted successfully");
	        } catch (IllegalArgumentException e) {
	            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Entreprise not found");
	        } catch (Exception e) {
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting entreprise");
	        }
	    }
	    @PutMapping("/entreprise/{id}")
	    public ResponseEntity<Map<String, String>> updateEntreprise(
	            @PathVariable UUID id,
	            @RequestParam("name") String name,
	            @RequestParam("adresse") String adresse,
	            @RequestParam("secteuractivite") String secteuractivite,
	            @RequestParam("Matricule") String Matricule,
	            @RequestParam("ville") String ville,
	            @RequestParam("siegesociale") String siegesociale,
	            @RequestParam("codeTVA") String codeTVA,
	            @RequestParam("logo") MultipartFile logo) {

	        Map<String, String> response = new HashMap<>();
	        try {
	            // Create an instance of EntrepriseDto and set its fields
	            EntrepriseDto entrepriseDto = new EntrepriseDto();
	            entrepriseDto.setName(name);
	            entrepriseDto.setAdresse(adresse);
	            entrepriseDto.setSecteuractivite(secteuractivite);
	            entrepriseDto.setMatricule(Matricule);
	            entrepriseDto.setVille(ville);
	            entrepriseDto.setSiegesociale(siegesociale);
	            entrepriseDto.setCodeTVA(codeTVA);

	            if (logo != null && !logo.isEmpty()) {
	                try {
	                    byte[] returnedImg = logo.getBytes(); // Converts the logo to a byte array
	                    entrepriseDto.setReturnedImg(returnedImg);
	                } catch (IOException e) {
	                    response.put("error", "Error reading logo file: " + e.getMessage());
	                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
	                }
	            }

	            entrepriseService.updateEntreprise(id, entrepriseDto);
	            response.put("message", "Entreprise updated successfully");
	            return ResponseEntity.ok(response);
	        } catch (Exception e) {
	            response.put("error", "Error updating entreprise: " + e.getMessage());
	            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
	        }
	    }
	    @GetMapping("/entreprises")
	    public ResponseEntity<List<EntrepriseDto>> getAllEntreprises() {
	        List<EntrepriseDto> entreprises = entrepriseService.getAllEntreprises();
	        return ResponseEntity.ok(entreprises);
	    }
	    @GetMapping("/user/{userId}/entreprises")
	    public ResponseEntity<?> getEntreprisesByUserId(@PathVariable UUID userId) {
	        try {
	            List<EntrepriseDto> entreprises = authService.getEntreprisesByUserId(userId);
	            return ResponseEntity.ok(entreprises);
	        } catch (RuntimeException e) {
	            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
	        }
	    }


}
