package it.unipi.wined.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
import java.util.ArrayList;

/**
 *
 * @author nicol
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class User {
    
    private String firstname;   
    private String lastname;    
    private String email;       
    private long phone;         
    private String birthday;    
    private String gender;      
    private String address;     
    private String nickname;    
    private String password;    
    
    //Un solo sistema di pagamento
    private PaymentInfo payment;
    
    //Lista di ordini dell'utente;
    private List<Order> orders;

    public enum Level {
        REGULAR, PREMIUM, ADMIN
    }

    public long id;
    private Level user_level;
    

    // =====================
    // COSTRUTTORI
    // =====================
    
    //Il costruttore vuoto lo richiede il down di Jackson se non c'è fa casino
    public User() {
        //Inizializzo a vuoto (sennò da Null Pointer)
        this.orders = new ArrayList<>();
    }

    //Costruttore senza order e payment
    //E' la versione iniziale degli utenti che ho messo nel dataset
    //Potrebbe esserci un user bo che vuole registrarsi, essere PREMIUM
    //Ma solo per vedere gli abbinamenti dei vini e non voler fare acquisti
    public User(String firstname,
                String lastname,
                String email,
                long phone,
                String birthday,
                String gender,
                String address,
                String nickname,
                String password,
                Level user_level) {

        this();
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.phone = phone;
        this.birthday = birthday;
        this.gender = gender;
        this.address = address;
        this.nickname = nickname;
        this.password = password;
        this.user_level = user_level;
    }
    
    //Costruttore completo previsto sia di payments che di ArrayOrders
    public User(String firstname,
                String lastname,
                String email,
                long phone,
                String birthday,
                String gender,
                String address,
                String nickname,
                String password,
                Level user_level,
                PaymentInfo payment,
                List<Order> orders){

        //Riuso il costruttore sopra
        this(firstname, lastname, email, phone, birthday, gender, address,
                nickname, password, user_level);
        
        this.payment = payment;
        
        if(orders == null){
            this.orders = new ArrayList<>();
        }else{
            this.orders = orders;
        }
    }

    // =====================
    // GETTER & SETTER
    // =====================
    
    public String getFirstName(){
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastName() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public long getPhone() {
        return phone;
    }

    public void setPhone(long phone) {
        this.phone = phone;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Level getUser_level() {
        return user_level;
    }

    public void setUser_level(Level user_level) {
        this.user_level = user_level;
    }

    public PaymentInfo getPayment(){
        return payment;
    }
    
    public void setPayment(PaymentInfo payment){
        this.payment = payment;
    }
    
    public List<Order> getOrders(){
        return orders;
    }
    
    public void setOrders(List<Order> orders){
        this.orders = orders;
    }
    
    public boolean[] getPrivileges() {
        if (user_level == null) {
            // Se l'utente non ha un livello impostato
            return new boolean[]{false, false, false};
        }
        switch (user_level) {
            case REGULAR:
                return new boolean[]{true, false, false};
            case PREMIUM:
                return new boolean[]{true, true, false};
            case ADMIN:
                return new boolean[]{true, true, true};
            default:
                return new boolean[]{false, false, false};
        }
    }

   @Override
    public String toString() {
    return "User{" +
            "firstname='" + firstname + '\'' +
            ", lastname='" + lastname + '\'' +
            ", email='" + email + '\'' +
            ", phone=" + phone +
            ", birthday='" + birthday + '\'' +
            ", gender='" + gender + '\'' +
            ", address='" + address + '\'' +
            ", nickname='" + nickname + '\'' +
            ", password='" + password + '\'' +
            ", user_level=" + user_level +
            ", orders=" + orders +
            '}';
    }

}
