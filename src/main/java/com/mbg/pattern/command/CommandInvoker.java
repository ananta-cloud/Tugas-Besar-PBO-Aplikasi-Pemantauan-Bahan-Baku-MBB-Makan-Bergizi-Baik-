package com.mbg.pattern.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
  CommandInvoker - Invoker dalam Command Pattern
  Berfungsi untuk:
  - Mengeksekusi command
  - Menyimpan history command yang telah dijalankan
  - Melakukan undo/redo terhadap command
**/
public class CommandInvoker {

    private Stack<Command> undoStack;
    private Stack<Command> redoStack;
    private List<Command> commandHistory;

    // Constructor

    public CommandInvoker() {
        this.undoStack = new Stack<>();
        this.redoStack = new Stack<>();
        this.commandHistory = new ArrayList<>();
    }

    public void executeCommand(Command command) throws Exception {
        try {
            System.out.println("\n[CommandInvoker] Executing: " + command.getDescription());
            
            // Eksekusi command
            command.execute();
            
            // Simpan ke undo stack
            undoStack.push(command);
            
            // Clear redo stack karena command baru dijalankan
            redoStack.clear();
            
            // Simpan ke history
            commandHistory.add(command);
            
            System.out.println("[CommandInvoker] Command berhasil dieksekusi");
            System.out.println("[CommandInvoker] Undo stack size: " + undoStack.size());
            
        } catch (Exception e) {
            System.err.println("[CommandInvoker] Error executing command: " + e.getMessage());
            throw e;
        }
    }

    // Melakukan undo terhadap command terakhir
    public void undoCommand() throws Exception {
        if (undoStack.isEmpty()) {
            throw new Exception("Tidak ada command yang dapat di-undo");
        }

        try {
            Command command = undoStack.pop();
            System.out.println("\n[CommandInvoker] Undoing: " + command.getDescription());
            
            // Undo command
            command.undo();
            
            // Pindahkan ke redo stack
            redoStack.push(command);
            
            System.out.println("[CommandInvoker] Undo berhasil");
            System.out.println("[CommandInvoker] Undo stack size: " + undoStack.size());
            System.out.println("[CommandInvoker] Redo stack size: " + redoStack.size());
            
        } catch (Exception e) {
            System.err.println("[CommandInvoker] Error during undo: " + e.getMessage());
            throw e;
        }
    }

    /**
     Melakukan redo terhadap command yang sebelumnya di-undo
     Menjalankan kembali command yang telah di-undo
    * */
    public void redoCommand() throws Exception {
        if (redoStack.isEmpty()) {
            throw new Exception("Tidak ada command yang dapat di-redo");
        }

        try {
            Command command = redoStack.pop();
            System.out.println("\n[CommandInvoker] Redoing: " + command.getDescription());
            
            // Re-execute command
            command.execute();
            
            // Pindahkan ke undo stack
            undoStack.push(command);
            
            System.out.println("[CommandInvoker] Redo berhasil");
            System.out.println("[CommandInvoker] Undo stack size: " + undoStack.size());
            System.out.println("[CommandInvoker] Redo stack size: " + redoStack.size());
            
        } catch (Exception e) {
            System.err.println("[CommandInvoker] Error during redo: " + e.getMessage());
            throw e;
        }
    }

    // Mendapatkan history semua command yang telah dijalankan
    public List<Command> getHistory() {
        return new ArrayList<>(commandHistory);
    }

    
    // Mendapatkan jumlah command yang dapat di-undo
    public int getUndoCount() {
        return undoStack.size();
    }

    
    // Mendapatkan jumlah command yang dapat di-redo
    public int getRedoCount() {
        return redoStack.size();
    }

    // Menampilkan history command
    public void printHistory() {
        System.out.println("\n========== COMMAND HISTORY ==========");
        if (commandHistory.isEmpty()) {
            System.out.println("Tidak ada command history");
        } else {
            for (int i = 0; i < commandHistory.size(); i++) {
                System.out.println((i + 1) + ". " + commandHistory.get(i).getDescription());
            }
        }
        System.out.println("=====================================\n");
    }

    // Clear semua history dan stack (untuk reset/logout)
    public void clearAll() {
        undoStack.clear();
        redoStack.clear();
        commandHistory.clear();
        System.out.println("[CommandInvoker] All command history cleared");
    }
}