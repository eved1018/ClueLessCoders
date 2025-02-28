/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package cluelesscoders.clueless;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

class Packet implements Serializable  {
    public int d;
    public String text;
    public ArrayList<Integer> arr;

    public Packet(int d, String text) {
        this.text = text;
        this.d = d;
        this.arr = new ArrayList<>(Arrays.asList(1,2,3));
    }

}
