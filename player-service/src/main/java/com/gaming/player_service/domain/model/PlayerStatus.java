package com.gaming.player_service.domain.model;

public enum PlayerStatus {
    IDLE,       // No está haciendo nada
    IN_QUEUE,   // Esperando matchmaking
    IN_MATCH    // Jugando una partida
}