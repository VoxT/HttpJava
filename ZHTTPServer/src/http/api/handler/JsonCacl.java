/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package http.api.handler;

/**
 *
 * @author Thieu Vo
 */
public class JsonCacl {
    public Integer number1;
    public Integer number2;

    public JsonCacl() {
    }
    
    public JsonCacl(Integer number1, Integer number2) {
        this.number1 = number1;
        this.number2 = number2;
    }
}
