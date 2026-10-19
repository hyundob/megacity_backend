package com.example.megacity_back.hydrogen;

final class HydrogenProblem extends RuntimeException {
    final int status;
    HydrogenProblem(int status, String message) { super(message); this.status = status; }
}
