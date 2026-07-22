#!/usr/bin/env python3
"""
PixelVerse Studio - Moteur de Jeu 2.5D Complet
Runner principal avec API Python, Jinja2 et serveur intégré
"""

import asyncio
import json
import sys
import os
import time
import random
import math
from pathlib import Path
from typing import Dict, List, Any, Optional, Callable
from dataclasses import dataclass, field
from enum import Enum
import traceback
import inspect
from functools import wraps

# Installation automatique des dépendances
try:
    from jinja2 import Environment, FileSystemLoader, Template
    from aiohttp import web, WSMsgType
    import aiohttp_jinja2
    import numpy as np
except ImportError:
    print("📦 Installation des dépendances...")
    os.system(f"{sys.executable} -m pip install jinja2 aiohttp aiohttp-jinja2 numpy")
    from jinja2 import Environment, FileSystemLoader, Template
    from aiohttp import web, WSMsgType
    import aiohttp_jinja2
    import numpy as np


# ===================== API DU MOTEUR DE JEU =====================

class GameEvent:
    """Événement de jeu"""
    def __init__(self, name: str, data: dict = None):
        self.name = name
        self.data = data or {}
        self.timestamp = time.time()

class Vector2:
    """Vecteur 2D"""
    def __init__(self, x: float = 0, y: float = 0):
        self.x = x
        self.y = y
    
    def __add__(self, other):
        return Vector2(self.x + other.x, self.y + other.y)
    
    def __mul__(self, scalar):
        return Vector2(self.x * scalar, self.y * scalar)
    
    def magnitude(self):
        return math.sqrt(self.x**2 + self.y**2)
    
    def normalize(self):
        mag = self.magnitude()
        if mag > 0:
            return Vector2(self.x/mag, self.y/mag)
        return Vector2()

class Vector3:
    """Vecteur 3D pour effets 2.5D"""
    def __init__(self, x: float = 0, y: float = 0, z: float = 0):
        self.x = x
        self.y = y
        self.z = z

class Color:
    """Couleur RGBA"""
    def __init__(self, r: int = 255, g: int = 255, b: int = 255, a: float = 1.0):
        self.r = min(255, max(0, r))
        self.g = min(255, max(0, g))
        self.b = min(255, max(0, b))
        self.a = min(1.0, max(0.0, a))
    
    @classmethod
    def from_hex(cls, hex_str: str):
        hex_str = hex_str.lstrip('#')
        return cls(int(hex_str[0:2], 16), int(hex_str[2:4], 16), int(hex_str[4:6], 16))
    
    def to_hex(self):
        return f"#{self.r:02x}{self.g:02x}{self.b:02x}"
    
    def to_css(self):
        return f"rgba({self.r},{self.g},{self.b},{self.a})"
    
    def darker(self, factor: float = 0.7):
        return Color(int(self.r*factor), int(self.g*factor), int(self.b*factor), self.a)
    
    def lighter(self, factor: float = 1.3):
        return Color(min(255, int(self.r*factor)), min(255, int(self.g*factor)), 
                    min(255, int(self.b*factor)), self.a)

class GameObject:
    """Objet de jeu de base"""
    _next_id = 0
    
    def __init__(self, name: str = "GameObject", position: Vector2 = None):
        GameObject._next_id += 1
        self.id = GameObject._next_id
        self.name = name
        self.position = position or Vector2()
        self.velocity = Vector2()
        self.acceleration = Vector2()
        self.rotation = 0.0
        self.scale = Vector2(1, 1)
        self.size = Vector2(50, 50)
        self.color = Color(100, 200, 255)
        self.visible = True
        self.active = True
        self.z_index = 0
        self.tags: List[str] = []
        self.components: Dict[str, Any] = {}
        self.properties: Dict[str, Any] = {}
        self.children: List['GameObject'] = []
        self.parent: Optional['GameObject'] = None
        
        # Physique
        self.mass = 1.0
        self.is_static = False
        self.gravity_scale = 1.0
        self.bounciness = 0.5
        self.friction = 0.3
        
        # Rendu
        self.texture = "📦"
        self.opacity = 1.0
        self.shadow = True
        self.border_color = Color(255, 255, 255, 0.5)
        self.border_width = 2
        
        # Animation
        self.animation_speed = 1.0
        self.animation_frame = 0
        self.animation_timer = 0.0
    
    def translate(self, dx: float, dy: float):
        self.position.x += dx
        self.position.y += dy
    
    def set_color(self, r: int, g: int, b: int, a: float = 1.0):
        self.color = Color(r, g, b, a)
    
    def add_component(self, name: str, component: Any):
        self.components[name] = component
    
    def get_component(self, name: str):
        return self.components.get(name)
    
    def add_child(self, child: 'GameObject'):
        child.parent = self
        self.children.append(child)
    
    def to_dict(self) -> dict:
        return {
            'id': self.id,
            'name': self.name,
            'x': self.position.x,
            'y': self.position.y,
            'z': self.z_index,
            'vx': self.velocity.x,
            'vy': self.velocity.y,
            'rotation': self.rotation,
            'width': self.size.x,
            'height': self.size.y,
            'color': self.color.to_css(),
            'texture': self.texture,
            'visible': self.visible,
            'active': self.active,
            'is_static': self.is_static,
            'opacity': self.opacity,
            'shadow': self.shadow,
            'border_color': self.border_color.to_css(),
            'border_width': self.border_width,
            'tags': self.tags,
            'properties': self.properties
        }

class Player(GameObject):
    """Joueur avec contrôles"""
    def __init__(self, name: str = "Player", position: Vector2 = None):
        super().__init__(name, position)
        self.texture = "🧑‍💻"
        self.speed = 8.0
        self.jump_force = -15.0
        self.is_jumping = False
        self.health = 100
        self.max_health = 100
        self.score = 0
        self.lives = 3
        self.tags = ["player", "controllable"]
        
    def move(self, direction: str, dt: float):
        if direction == "left":
            self.position.x -= self.speed
        elif direction == "right":
            self.position.x += self.speed
        elif direction == "up":
            self.position.y -= self.speed
        elif direction == "down":
            self.position.y += self.speed
    
    def jump(self):
        if not self.is_jumping:
            self.velocity.y = self.jump_force
            self.is_jumping = True
    
    def take_damage(self, amount: int):
        self.health -= amount
        if self.health <= 0:
            self.health = 0
            self.lives -= 1
            return True
        return False
    
    def heal(self, amount: int):
        self.health = min(self.max_health, self.health + amount)

class Enemy(GameObject):
    """Ennemi avec IA basique"""
    def __init__(self, name: str = "Enemy", position: Vector2 = None):
        super().__init__(name, position)
        self.texture = "👾"
        self.speed = 3.0
        self.damage = 10
        self.patrol_range = 200
        self.start_position = position or Vector2()
        self.direction = 1
        self.tags = ["enemy"]
        self.color = Color(255, 70, 70)
    
    def update_ai(self, dt: float, player_position: Vector2 = None):
        # Patrouille simple
        self.position.x += self.speed * self.direction
        
        if abs(self.position.x - self.start_position.x) > self.patrol_range:
            self.direction *= -1

class Collectible(GameObject):
    """Objet à collecter"""
    def __init__(self, name: str = "Coin", position: Vector2 = None):
        super().__init__(name, position)
        self.texture = "⭐"
        self.value = 10
        self.collect_effect = "sparkle"
        self.tags = ["collectible"]
        self.size = Vector2(30, 30)
        self.bob_speed = 2.0
        self.bob_height = 10
        self.base_y = position.y if position else 0
    
    def update_animation(self, time: float):
        if self.base_y == 0:
            self.base_y = self.position.y
        self.position.y = self.base_y + math.sin(time * self.bob_speed) * self.bob_height

class Platform(GameObject):
    """Plateforme"""
    def __init__(self, name: str = "Platform", position: Vector2 = None, 
                 width: float = 200, height: float = 40):
        super().__init__(name, position)
        self.texture = "🏗️"
        self.is_static = True
        self.size = Vector2(width, height)
        self.color = Color(139, 90, 43)
        self.tags = ["platform", "ground"]

class Particle:
    """Particule pour effets visuels"""
    def __init__(self, position: Vector2, velocity: Vector2, 
                 lifetime: float = 1.0, color: Color = None, size: float = 4):
        self.position = position
        self.velocity = velocity
        self.lifetime = lifetime
        self.max_lifetime = lifetime
        self.color = color or Color(255, 255, 255)
        self.size = size
    
    @property
    def alive(self):
        return self.lifetime > 0
    
    def update(self, dt: float):
        self.lifetime -= dt
        self.position.x += self.velocity.x * dt
        self.position.y += self.velocity.y * dt
        self.velocity.y += 200 * dt  # Gravité
    
    def to_dict(self):
        alpha = self.lifetime / self.max_lifetime
        return {
            'x': self.position.x,
            'y': self.position.y,
            'size': self.size,
            'color': self.color.to_css(),
            'alpha': alpha
        }

class Camera:
    """Caméra 2.5D"""
    def __init__(self, width: int = 800, height: int = 600):
        self.position = Vector2()
        self.target = None
        self.zoom = 1.0
        self.rotation = 0.0
        self.width = width
        self.height = height
        self.smooth_speed = 5.0
    
    def follow(self, target: GameObject, dt: float):
        if target:
            target_pos = Vector2(
                target.position.x - self.width/2,
                target.position.y - self.height/2
            )
            self.position.x += (target_pos.x - self.position.x) * self.smooth_speed * dt
            self.position.y += (target_pos.y - self.position.y) * self.smooth_speed * dt
    
    def world_to_screen(self, world_pos: Vector2) -> Vector2:
        return Vector2(
            (world_pos.x - self.position.x) * self.zoom + self.width/2,
            (world_pos.y - self.position.y) * self.zoom + self.height/2
        )

class InputManager:
    """Gestionnaire d'entrées"""
    def __init__(self):
        self.keys_pressed: set = set()
        self.keys_just_pressed: set = set()
        self.keys_released: set = set()
        self.mouse_position = Vector2()
        self.mouse_buttons: set = set()
        self.touch_position = Vector2()
        self.touch_active = False
    
    def is_key_pressed(self, key: str) -> bool:
        return key.lower() in self.keys_pressed
    
    def is_key_just_pressed(self, key: str) -> bool:
        return key.lower() in self.keys_just_pressed
    
    def is_key_released(self, key: str) -> bool:
        return key.lower() in self.keys_released
    
    def clear_frame_events(self):
        self.keys_just_pressed.clear()
        self.keys_released.clear()

class AudioManager:
    """Gestionnaire audio"""
    def __init__(self):
        self.sounds: Dict[str, dict] = {}
        self.music_volume = 0.5
        self.sfx_volume = 1.0
    
    def load_sound(self, name: str, url: str):
        self.sounds[name] = {'url': url, 'playing': False}
    
    def play_sound(self, name: str):
        if name in self.sounds:
            self.sounds[name]['playing'] = True
            return {'action': 'play_sound', 'name': name, 'url': self.sounds[name]['url']}
        return None
    
    def stop_sound(self, name: str):
        if name in self.sounds:
            self.sounds[name]['playing'] = False

class Scene:
    """Scène de jeu"""
    def __init__(self, name: str = "Main Scene"):
        self.name = name
        self.objects: List[GameObject] = []
        self.particles: List[Particle] = []
        self.camera = Camera()
        self.input = InputManager()
        self.audio = AudioManager()
        self.time_scale = 1.0
        self.elapsed_time = 0.0
        self.frame_count = 0
        self.fps = 0
    
    def add_object(self, obj: GameObject):
        self.objects.append(obj)
        return obj
    
    def remove_object(self, obj: GameObject):
        if obj in self.objects:
            self.objects.remove(obj)
    
    def find_object(self, name: str) -> Optional[GameObject]:
        for obj in self.objects:
            if obj.name == name:
                return obj
        return None
    
    def find_objects_by_tag(self, tag: str) -> List[GameObject]:
        return [obj for obj in self.objects if tag in obj.tags]
    
    def spawn_particle(self, position: Vector2, count: int = 10):
        for _ in range(count):
            velocity = Vector2(
                random.uniform(-100, 100),
                random.uniform(-200, -50)
            )
            color = Color(
                random.randint(100, 255),
                random.randint(100, 255),
                random.randint(100, 255)
            )
            self.particles.append(Particle(position, velocity, 
                                          random.uniform(0.5, 1.5), color))
    
    def update(self, dt: float):
        dt *= self.time_scale
        
        # Mettre à jour les objets
        for obj in self.objects:
            if not obj.active:
                continue
            
            # Physique simple
            if not obj.is_static:
                obj.velocity.y += 9.81 * obj.gravity_scale * dt * 60
                obj.position.x += obj.velocity.x * dt * 60
                obj.position.y += obj.velocity.y * dt * 60
            
            # Animation
            if hasattr(obj, 'update_animation'):
                obj.update_animation(self.elapsed_time)
        
        # Mettre à jour les particules
        self.particles = [p for p in self.particles if p.alive]
        for p in self.particles:
            p.update(dt)
        
        # Mettre à jour la caméra
        if self.camera.target:
            self.camera.follow(self.camera.target, dt)
        
        # Calculer les FPS
        self.frame_count += 1
        if self.elapsed_time >= 1.0:
            self.fps = self.frame_count
            self.frame_count = 0
            self.elapsed_time = 0
        else:
            self.elapsed_time += dt
        
        # Nettoyer les événements d'entrée
        self.input.clear_frame_events()
    
    def to_dict(self) -> dict:
        return {
            'name': self.name,
            'objects': [obj.to_dict() for obj in self.objects if obj.visible],
            'particles': [p.to_dict() for p in self.particles],
            'camera': {
                'x': self.camera.position.x,
                'y': self.camera.position.y,
                'zoom': self.camera.zoom
            },
            'fps': self.fps,
            'elapsed_time': self.elapsed_time
        }

class GameEngine:
    """Moteur de jeu principal"""
    def __init__(self):
        self.scenes: Dict[str, Scene] = {}
        self.current_scene: Optional[Scene] = None
        self.running = False
        self.last_time = 0
        self._event_handlers: Dict[str, List[Callable]] = {}
        
        # Créer la scène par défaut
        self.create_scene("main")
    
    def create_scene(self, name: str) -> Scene:
        scene = Scene(name)
        self.scenes[name] = scene
        if not self.current_scene:
            self.current_scene = scene
        return scene
    
    def load_scene(self, name: str):
        if name in self.scenes:
            self.current_scene = self.scenes[name]
    
    def on(self, event_name: str, handler: Callable):
        if event_name not in self._event_handlers:
            self._event_handlers[event_name] = []
        self._event_handlers[event_name].append(handler)
    
    def emit(self, event_name: str, data: dict = None):
        if event_name in self._event_handlers:
            for handler in self._event_handlers[event_name]:
                handler(GameEvent(event_name, data))
    
    def update(self, dt: float):
        if self.current_scene:
            self.current_scene.update(dt)
            self.emit('update', {'dt': dt})
    
    def get_state(self) -> dict:
        if self.current_scene:
            return self.current_scene.to_dict()
        return {}

# ===================== API PYTHON POUR LES SCRIPTS =====================

class PixelAPI:
    """API Python exposée aux scripts utilisateur"""
    
    def __init__(self, engine: GameEngine):
        self.engine = engine
        self.scene = engine.current_scene
    
    # Création d'objets
    def create_player(self, x: float = 400, y: float = 300, name: str = "Player") -> Player:
        player = Player(name, Vector2(x, y))
        self.scene.add_object(player)
        return player
    
    def create_enemy(self, x: float = 200, y: float = 300, name: str = "Enemy") -> Enemy:
        enemy = Enemy(name, Vector2(x, y))
        self.scene.add_object(enemy)
        return enemy
    
    def create_coin(self, x: float = 0, y: float = 0) -> Collectible:
        coin = Collectible(f"Coin_{len(self.scene.objects)}", Vector2(x, y))
        self.scene.add_object(coin)
        return coin
    
    def create_platform(self, x: float = 400, y: float = 500, 
                       width: float = 300, height: float = 40) -> Platform:
        platform = Platform(f"Platform_{len(self.scene.objects)}", 
                           Vector2(x, y), width, height)
        self.scene.add_object(platform)
        return platform
    
    def create_object(self, name: str = "Object", x: float = 0, y: float = 0,
                     texture: str = "📦", color: str = "#ffffff", 
                     width: float = 50, height: float = 50) -> GameObject:
        obj = GameObject(name, Vector2(x, y))
        obj.texture = texture
        obj.color = Color.from_hex(color)
        obj.size = Vector2(width, height)
        self.scene.add_object(obj)
        return obj
    
    # Utilitaires
    def random_range(self, min_val: float, max_val: float) -> float:
        return random.uniform(min_val, max_val)
    
    def distance(self, obj1: GameObject, obj2: GameObject) -> float:
        dx = obj1.position.x - obj2.position.x
        dy = obj1.position.y - obj2.position.y
        return math.sqrt(dx**2 + dy**2)
    
    def check_collision(self, obj1: GameObject, obj2: GameObject) -> bool:
        return (abs(obj1.position.x - obj2.position.x) < (obj1.size.x + obj2.size.x) / 2 and
                abs(obj1.position.y - obj2.position.y) < (obj1.size.y + obj2.size.y) / 2)
    
    def spawn_particles(self, x: float, y: float, count: int = 10):
        self.scene.spawn_particle(Vector2(x, y), count)
    
    def set_camera_target(self, obj: GameObject):
        self.scene.camera.target = obj
    
    def get_mouse_position(self) -> Vector2:
        return self.scene.input.mouse_position
    
    def is_key_pressed(self, key: str) -> bool:
        return self.scene.input.is_key_pressed(key)
    
    def log(self, *args):
        print(f"[PixelAPI]", *args)
    
    def get_time(self) -> float:
        return time.time()
    
    def load_scene(self, name: str):
        self.engine.load_scene(name)
        self.scene = self.engine.current_scene

# ===================== GESTIONNAIRE DE SCRIPTS =====================

class ScriptManager:
    """Gère l'exécution des scripts Python utilisateur"""
    
    def __init__(self, engine: GameEngine):
        self.engine = engine
        self.api = PixelAPI(engine)
        self.user_globals = {
            'api': self.api,
            'engine': self.engine,
            'scene': engine.current_scene,
            'print': self.api.log,
            'time': time,
            'math': math,
            'random': random,
            'Vector2': Vector2,
            'Vector3': Vector3,
            'Color': Color,
            'Player': Player,
            'Enemy': Enemy,
            'Collectible': Collectible,
            'Platform': Platform,
            'GameObject': GameObject
        }
        self.update_functions = []
    
    def execute_script(self, code: str):
        """Exécute un script Python utilisateur"""
        try:
            # Compiler et exécuter le code
            compiled = compile(code, '<user_script>', 'exec')
            exec(compiled, self.user_globals)
            
            # Récupérer la fonction de mise à jour si elle existe
            if 'update' in self.user_globals:
                self.update_functions.append(self.user_globals['update'])
            
            # Récupérer la fonction d'initialisation si elle existe
            if 'init' in self.user_globals:
                self.user_globals['init']()
            
            return True, "Script exécuté avec succès"
        except Exception as e:
            return False, f"Erreur: {str(e)}\n{traceback.format_exc()}"
    
    def call_update(self, dt: float):
        """Appelle toutes les fonctions de mise à jour"""
        for func in self.update_functions:
            try:
                func(dt)
            except Exception as e:
                print(f"Erreur dans update: {e}")
    
    def reset(self):
        """Réinitialise le gestionnaire de scripts"""
        self.update_functions.clear()
        self.user_globals.update({
            'api': self.api,
            'engine': self.engine,
            'scene': self.engine.current_scene
        })

# ===================== SERVEUR WEB =====================

class GameServer:
    """Serveur WebSocket pour le moteur de jeu"""
    
    def __init__(self, host: str = '0.0.0.0', port: int = 8080):
        self.host = host
        self.port = port
        self.engine = GameEngine()
        self.script_manager = ScriptManager(self.engine)
        self.clients = set()
        self.app = web.Application()
        self.setup_routes()
        
        # Créer le template HTML
        self.html_template = None
        self.create_html_template()
    
    def create_html_template(self):
        """Crée le template Jinja2 pour le rendu HTML"""
        template_str = '''<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no, viewport-fit=cover">
    <meta name="theme-color" content="#1a1a2e">
    <meta name="apple-mobile-web-app-capable" content="yes">
    <title>🎮 PixelVerse Studio</title>
    <style>
        :root {
            --bg: #0a0a1a;
            --surface: #12122a;
            --accent: #4ecdc4;
            --danger: #ff6b6b;
            --warning: #ffe66d;
            --text: #ffffff;
            --text-secondary: #b0b0d0;
            --border: #2a2a4a;
            --safe-bottom: env(safe-area-inset-bottom, 0px);
        }

        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
            -webkit-tap-highlight-color: transparent;
        }

        body {
            background: var(--bg);
            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', system-ui, sans-serif;
            height: 100vh;
            height: 100dvh;
            overflow: hidden;
            display: flex;
            flex-direction: column;
            touch-action: manipulation;
            user-select: none;
        }

        #header {
            background: linear-gradient(135deg, #667eea, #764ba2);
            padding: 15px;
            display: flex;
            justify-content: space-between;
            align-items: center;
            z-index: 10;
        }

        #header h1 {
            font-size: 20px;
            background: linear-gradient(to right, #fff, #ffe66d);
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
            font-weight: 800;
        }

        .status {
            display: flex;
            align-items: center;
            gap: 10px;
        }

        .status-dot {
            width: 10px;
            height: 10px;
            border-radius: 50%;
            background: #4ecdc4;
            animation: pulse 2s infinite;
        }

        @keyframes pulse {
            0%, 100% { opacity: 1; }
            50% { opacity: 0.5; }
        }

        #main-container {
            flex: 1;
            display: flex;
            overflow: hidden;
        }

        @media (max-width: 768px) {
            #main-container {
                flex-direction: column;
            }
        }

        #editor-panel {
            width: 350px;
            background: var(--surface);
            display: flex;
            flex-direction: column;
            border-right: 2px solid var(--border);
        }

        @media (max-width: 768px) {
            #editor-panel {
                width: 100%;
                max-height: 40vh;
                border-right: none;
                border-top: 2px solid var(--border);
            }
        }

        .tab-bar {
            display: flex;
            background: rgba(0,0,0,0.3);
        }

        .tab {
            flex: 1;
            padding: 12px;
            text-align: center;
            cursor: pointer;
            font-weight: 600;
            font-size: 13px;
            color: var(--text-secondary);
            border-bottom: 3px solid transparent;
            transition: all 0.3s;
        }

        .tab.active {
            color: var(--accent);
            border-bottom-color: var(--accent);
            background: rgba(78, 205, 196, 0.1);
        }

        .tab-content {
            display: none;
            flex: 1;
            flex-direction: column;
            overflow: hidden;
        }

        .tab-content.active {
            display: flex;
        }

        #code-editor {
            flex: 1;
            background: #0d1117;
            color: #58a6ff;
            border: none;
            padding: 15px;
            font-family: 'JetBrains Mono', 'Fira Code', 'Consolas', monospace;
            font-size: 13px;
            line-height: 1.6;
            resize: none;
            outline: none;
            tab-size: 4;
        }

        .button-bar {
            display: grid;
            grid-template-columns: repeat(4, 1fr);
            gap: 5px;
            padding: 10px;
            background: rgba(0,0,0,0.3);
        }

        .btn {
            padding: 10px;
            border: none;
            border-radius: 8px;
            font-weight: 700;
            font-size: 11px;
            cursor: pointer;
            transition: all 0.3s;
            text-transform: uppercase;
            letter-spacing: 0.5px;
            display: flex;
            align-items: center;
            justify-content: center;
            gap: 4px;
        }

        .btn:active {
            transform: scale(0.95);
        }

        .btn-run { background: linear-gradient(135deg, #00d2ff, #3a7bd5); color: white; }
        .btn-stop { background: linear-gradient(135deg, #f857a6, #ff5858); color: white; }
        .btn-reset { background: linear-gradient(135deg, #667eea, #764ba2); color: white; }
        .btn-save { background: linear-gradient(135deg, #ffe66d, #f7b733); color: #333; }

        #assets-panel {
            display: grid;
            grid-template-columns: repeat(4, 1fr);
            gap: 8px;
            padding: 10px;
            overflow-y: auto;
            max-height: 200px;
        }

        .asset {
            aspect-ratio: 1;
            background: rgba(255,255,255,0.05);
            border-radius: 12px;
            border: 2px solid var(--border);
            cursor: pointer;
            display: flex;
            flex-direction: column;
            align-items: center;
            justify-content: center;
            transition: all 0.3s;
            font-size: 24px;
        }

        .asset:hover {
            border-color: var(--accent);
            background: rgba(78, 205, 196, 0.1);
        }

        .asset-label {
            font-size: 9px;
            color: var(--text-secondary);
            margin-top: 5px;
        }

        #game-area {
            flex: 1;
            position: relative;
            background: radial-gradient(ellipse at center, #1a1a3a 0%, #0a0a1a 100%);
        }

        canvas {
            width: 100%;
            height: 100%;
            display: block;
            image-rendering: pixelated;
        }

        #fps-counter {
            position: absolute;
            top: 10px;
            right: 10px;
            background: rgba(0,0,0,0.6);
            color: var(--warning);
            padding: 5px 10px;
            border-radius: 20px;
            font-size: 12px;
            font-weight: bold;
            backdrop-filter: blur(10px);
        }

        .touch-controls {
            position: absolute;
            bottom: calc(20px + var(--safe-bottom));
            left: 20px;
            right: 20px;
            display: flex;
            justify-content: space-between;
            pointer-events: none;
        }

        .joystick {
            width: 100px;
            height: 100px;
            background: rgba(255,255,255,0.1);
            border-radius: 50%;
            border: 2px solid rgba(255,255,255,0.2);
            pointer-events: all;
            backdrop-filter: blur(10px);
        }

        .action-btns {
            display: flex;
            gap: 10px;
            pointer-events: all;
        }

        .action-btn {
            width: 50px;
            height: 50px;
            border-radius: 50%;
            background: rgba(255,255,255,0.15);
            border: 2px solid rgba(255,255,255,0.3);
            color: white;
            font-size: 20px;
            display: flex;
            align-items: center;
            justify-content: center;
            cursor: pointer;
        }

        #console {
            background: rgba(0,0,0,0.8);
            color: var(--accent);
            padding: 10px;
            font-family: 'JetBrains Mono', monospace;
            font-size: 11px;
            max-height: 150px;
            overflow-y: auto;
        }

        .console-line {
            padding: 2px 0;
            border-bottom: 1px solid rgba(255,255,255,0.05);
        }

        .inspector-item {
            padding: 10px;
            margin: 5px;
            background: rgba(255,255,255,0.05);
            border-radius: 8px;
            cursor: pointer;
            border-left: 3px solid var(--accent);
            transition: all 0.3s;
        }

        .inspector-item:hover {
            background: rgba(255,255,255,0.1);
            transform: translateX(5px);
        }
    </style>
</head>
<body>
    <div id="header">
        <h1>🎮 PixelVerse Studio</h1>
        <div class="status">
            <span id="status-text">Connecté</span>
            <div class="status-dot"></div>
            <span id="fps-display">60 FPS</span>
        </div>
    </div>

    <div id="main-container">
        <div id="editor-panel">
            <div class="tab-bar">
                <div class="tab active" data-tab="script">💻 Script</div>
                <div class="tab" data-tab="assets">🎨 Assets</div>
                <div class="tab" data-tab="inspector">🔍 Inspecteur</div>
            </div>

            <div class="tab-content active" id="tab-script">
                <textarea id="code-editor" spellcheck="false">{{ default_script }}</textarea>
            </div>

            <div class="tab-content" id="tab-assets">
                <div id="assets-panel">
                    {% for asset in assets %}
                    <div class="asset" data-asset='{{ asset|tojson }}' draggable="true">
                        {{ asset.icon }}
                        <span class="asset-label">{{ asset.name }}</span>
                    </div>
                    {% endfor %}
                </div>
            </div>

            <div class="tab-content" id="tab-inspector">
                <div id="inspector-content">
                    <p style="color: var(--text-secondary); padding: 10px;">
                        Sélectionnez un objet dans la scène
                    </p>
                </div>
            </div>

            <div class="button-bar">
                <button class="btn btn-run" id="btn-run">▶ Run</button>
                <button class="btn btn-stop" id="btn-stop">■ Stop</button>
                <button class="btn btn-reset" id="btn-reset">↺ Reset</button>
                <button class="btn btn-save" id="btn-save">💾 Save</button>
            </div>

            <div id="console"></div>
        </div>

        <div id="game-area">
            <canvas id="game-canvas"></canvas>
            <div id="fps-counter">0 FPS</div>
            
            <div class="touch-controls">
                <div class="joystick" id="joystick"></div>
                <div class="action-btns">
                    <div class="action-btn" data-action="jump">🦘</div>
                    <div class="action-btn" data-action="attack">⚔️</div>
                </div>
            </div>
        </div>
    </div>

    <script>
        // ==================== CLIENT WEBSOCKET ====================
        class GameClient {
            constructor() {
                this.ws = null;
                this.canvas = document.getElementById('game-canvas');
                this.ctx = this.canvas.getContext('2d', {
                    alpha: true,
                    antialias: true,
                    desynchronized: true
                });
                this.keys = new Set();
                this.touchPos = { x: 0, y: 0 };
                this.touchActive = false;
                this.gameState = null;
                this.fps = 0;
                this.lastFrameTime = 0;
                this.frameCount = 0;
                this.fpsTimer = 0;
                
                this.resizeCanvas();
                this.setupEventListeners();
                this.connect();
                
                window.addEventListener('resize', () => this.resizeCanvas());
            }
            
            resizeCanvas() {
                const container = document.getElementById('game-area');
                const dpr = window.devicePixelRatio || 1;
                this.canvas.width = container.clientWidth * dpr;
                this.canvas.height = container.clientHeight * dpr;
                this.canvas.style.width = container.clientWidth + 'px';
                this.canvas.style.height = container.clientHeight + 'px';
                this.ctx.setTransform(dpr, 0, 0, dpr, 0, 0);
                this.width = container.clientWidth;
                this.height = container.clientHeight;
            }
            
            connect() {
                const protocol = location.protocol === 'https:' ? 'wss:' : 'ws:';
                const wsUrl = `${protocol}//${location.host}/ws`;
                
                this.ws = new WebSocket(wsUrl);
                
                this.ws.onopen = () => {
                    this.log('✅ Connecté au serveur');
                    document.getElementById('status-text').textContent = 'Connecté';
                };
                
                this.ws.onmessage = (event) => {
                    const data = JSON.parse(event.data);
                    
                    if (data.type === 'state') {
                        this.gameState = data.state;
                        this.render();
                    } else if (data.type === 'log') {
                        this.log(data.message);
                    } else if (data.type === 'error') {
                        this.log('❌ ' + data.message, 'error');
                    }
                };
                
                this.ws.onclose = () => {
                    this.log('⚠️ Déconnecté, reconnexion...');
                    document.getElementById('status-text').textContent = 'Déconnecté';
                    setTimeout(() => this.connect(), 2000);
                };
                
                this.ws.onerror = (err) => {
                    this.log('❌ Erreur WebSocket');
                };
            }
            
            send(data) {
                if (this.ws && this.ws.readyState === WebSocket.OPEN) {
                    this.ws.send(JSON.stringify(data));
                }
            }
            
            setupEventListeners() {
                // Clavier
                window.addEventListener('keydown', (e) => {
                    const key = this.getKeyName(e.key);
                    if (!this.keys.has(key)) {
                        this.keys.add(key);
                        this.send({ type: 'keydown', key: key });
                    }
                    e.preventDefault();
                });
                
                window.addEventListener('keyup', (e) => {
                    const key = this.getKeyName(e.key);
                    this.keys.delete(key);
                    this.send({ type: 'keyup', key: key });
                });
                
                // Joystick tactile
                const joystick = document.getElementById('joystick');
                
                joystick.addEventListener('touchstart', (e) => {
                    this.touchActive = true;
                    this.handleJoystick(e.touches[0]);
                });
                
                joystick.addEventListener('touchmove', (e) => {
                    if (this.touchActive) {
                        this.handleJoystick(e.touches[0]);
                    }
                });
                
                joystick.addEventListener('touchend', () => {
                    this.touchActive = false;
                    this.send({ type: 'keyup', key: 'left' });
                    this.send({ type: 'keyup', key: 'right' });
                    this.send({ type: 'keyup', key: 'up' });
                    this.send({ type: 'keyup', key: 'down' });
                });
                
                // Boutons d'action
                document.querySelectorAll('.action-btn').forEach(btn => {
                    btn.addEventListener('touchstart', () => {
                        const action = btn.dataset.action;
                        this.send({ type: 'keydown', key: action });
                    });
                    
                    btn.addEventListener('touchend', () => {
                        const action = btn.dataset.action;
                        this.send({ type: 'keyup', key: action });
                    });
                });
                
                // Boutons de l'interface
                document.getElementById('btn-run').addEventListener('click', () => {
                    const code = document.getElementById('code-editor').value;
                    this.send({ type: 'run_script', code: code });
                });
                
                document.getElementById('btn-stop').addEventListener('click', () => {
                    this.send({ type: 'stop' });
                });
                
                document.getElementById('btn-reset').addEventListener('click', () => {
                    this.send({ type: 'reset' });
                });
                
                document.getElementById('btn-save').addEventListener('click', () => {
                    const code = document.getElementById('code-editor').value;
                    localStorage.setItem('pixelverse_script', code);
                    this.log('💾 Script sauvegardé');
                });
                
                // Onglets
                document.querySelectorAll('.tab').forEach(tab => {
                    tab.addEventListener('click', () => {
                        document.querySelectorAll('.tab').forEach(t => t.classList.remove('active'));
                        document.querySelectorAll('.tab-content').forEach(c => c.classList.remove('active'));
                        
                        tab.classList.add('active');
                        document.getElementById(`tab-${tab.dataset.tab}`).classList.add('active');
                    });
                });
                
                // Assets
                document.querySelectorAll('.asset').forEach(asset => {
                    asset.addEventListener('click', () => {
                        const data = JSON.parse(asset.dataset.asset);
                        this.send({ type: 'create_asset', asset: data });
                    });
                });
                
                // Canvas clic pour sélection
                this.canvas.addEventListener('click', (e) => {
                    const rect = this.canvas.getBoundingClientRect();
                    const x = e.clientX - rect.left;
                    const y = e.clientY - rect.top;
                    this.send({ type: 'click', x: x, y: y });
                });
                
                // Charger le script sauvegardé
                const saved = localStorage.getItem('pixelverse_script');
                if (saved) {
                    document.getElementById('code-editor').value = saved;
                }
            }
            
            handleJoystick(touch) {
                const rect = touch.target.getBoundingClientRect();
                const cx = rect.left + rect.width / 2;
                const cy = rect.top + rect.height / 2;
                const dx = touch.clientX - cx;
                const dy = touch.clientY - cy;
                const dist = Math.sqrt(dx * dx + dy * dy);
                const threshold = 15;
                
                // Réinitialiser
                ['left', 'right', 'up', 'down'].forEach(k => {
                    if (this.keys.has(k)) {
                        this.keys.delete(k);
                        this.send({ type: 'keyup', key: k });
                    }
                });
                
                if (dist > threshold) {
                    if (Math.abs(dx) > Math.abs(dy)) {
                        const key = dx > 0 ? 'right' : 'left';
                        this.keys.add(key);
                        this.send({ type: 'keydown', key: key });
                    } else {
                        const key = dy > 0 ? 'down' : 'up';
                        this.keys.add(key);
                        this.send({ type: 'keydown', key: key });
                    }
                }
            }
            
            getKeyName(key) {
                const map = {
                    'ArrowLeft': 'left', 'ArrowRight': 'right',
                    'ArrowUp': 'up', 'ArrowDown': 'down',
                    'q': 'left', 'd': 'right',
                    'z': 'up', 's': 'down',
                    ' ': 'jump', 'Enter': 'attack'
                };
                return map[key] || key.toLowerCase();
            }
            
            render() {
                if (!this.gameState) return;
                
                const ctx = this.ctx;
                const state = this.gameState;
                
                // Effacer
                ctx.clearRect(0, 0, this.width, this.height);
                
                // Fond
                const gradient = ctx.createLinearGradient(0, 0, 0, this.height);
                gradient.addColorStop(0, '#0a0a1a');
                gradient.addColorStop(0.5, '#1a1a3a');
                gradient.addColorStop(1, '#0f0f2a');
                ctx.fillStyle = gradient;
                ctx.fillRect(0, 0, this.width, this.height);
                
                // Grille
                ctx.strokeStyle = 'rgba(255, 255, 255, 0.03)';
                ctx.lineWidth = 1;
                for (let x = 0; x < this.width; x += 50) {
                    ctx.beginPath();
                    ctx.moveTo(x, 0);
                    ctx.lineTo(x, this.height);
                    ctx.stroke();
                }
                for (let y = 0; y < this.height; y += 50) {
                    ctx.beginPath();
                    ctx.moveTo(0, y);
                    ctx.lineTo(this.width, y);
                    ctx.stroke();
                }
                
                // Objets
                if (state.objects) {
                    state.objects.forEach(obj => {
                        if (!obj.visible) return;
                        
                        ctx.save();
                        
                        // Appliquer la caméra
                        const camX = state.camera ? state.camera.x : 0;
                        const camY = state.camera ? state.camera.y : 0;
                        const zoom = state.camera ? state.camera.zoom : 1;
                        
                        const screenX = (obj.x - camX) * zoom + this.width / 2;
                        const screenY = (obj.y - camY) * zoom + this.height / 2;
                        
                        ctx.translate(screenX, screenY);
                        ctx.rotate((obj.rotation || 0) * Math.PI / 180);
                        
                        // Ombre
                        if (obj.shadow) {
                            ctx.fillStyle = 'rgba(0, 0, 0, 0.4)';
                            ctx.fillRect(-obj.width/2 + 5, -obj.height/2 + 5, 
                                        obj.width, obj.height);
                        }
                        
                        // Corps avec dégradé
                        const objGrad = ctx.createLinearGradient(
                            -obj.width/2, -obj.height/2,
                            obj.width/2, obj.height/2
                        );
                        objGrad.addColorStop(0, obj.color);
                        objGrad.addColorStop(1, 'rgba(0,0,0,0.3)');
                        
                        ctx.fillStyle = objGrad;
                        ctx.globalAlpha = obj.opacity || 1;
                        ctx.fillRect(-obj.width/2, -obj.height/2, obj.width, obj.height);
                        
                        // Texture
                        if (obj.texture) {
                            ctx.fillStyle = 'white';
                            ctx.globalAlpha = 1;
                            ctx.font = `${obj.height * 0.5}px Arial`;
                            ctx.textAlign = 'center';
                            ctx.textBaseline = 'middle';
                            ctx.fillText(obj.texture, 0, 0);
                        }
                        
                        // Bordure
                        if (obj.border_width > 0) {
                            ctx.strokeStyle = obj.border_color || 'rgba(255,255,255,0.5)';
                            ctx.lineWidth = obj.border_width;
                            ctx.globalAlpha = 1;
                            ctx.strokeRect(-obj.width/2, -obj.height/2, obj.width, obj.height);
                        }
                        
                        ctx.restore();
                    });
                }
                
                // Particules
                if (state.particles) {
                    state.particles.forEach(p => {
                        const camX = state.camera ? state.camera.x : 0;
                        const camY = state.camera ? state.camera.y : 0;
                        const zoom = state.camera ? state.camera.zoom : 1;
                        
                        const px = (p.x - camX) * zoom + this.width / 2;
                        const py = (p.y - camY) * zoom + this.height / 2;
                        
                        ctx.fillStyle = p.color;
                        ctx.globalAlpha = p.alpha;
                        ctx.fillRect(px - p.size/2, py - p.size/2, p.size, p.size);
                    });
                    ctx.globalAlpha = 1;
                }
                
                // FPS
                this.frameCount++;
                const now = performance.now();
                const dt = (now - this.lastFrameTime) / 1000;
                this.lastFrameTime = now;
                this.fpsTimer += dt;
                
                if (this.fpsTimer >= 1) {
                    this.fps = Math.round(this.frameCount / this.fpsTimer);
                    document.getElementById('fps-counter').textContent = 
                        `${this.fps} FPS`;
                    document.getElementById('fps-display').textContent = 
                        `${this.fps} FPS`;
                    this.frameCount = 0;
                    this.fpsTimer = 0;
                }
            }
            
            log(message, type = 'info') {
                const console = document.getElementById('console');
                const line = document.createElement('div');
                line.className = 'console-line';
                line.textContent = `> ${message}`;
                if (type === 'error') {
                    line.style.color = '#ff6b6b';
                }
                console.appendChild(line);
                console.scrollTop = console.scrollHeight;
                
                // Limiter à 100 lignes
                while (console.children.length > 100) {
                    console.removeChild(console.firstChild);
                }
            }
        }
        
        // Démarrer le client
        const client = new GameClient();
    </script>
</body>
</html>'''
        
        self.html_template = Template(template_str)
    
    def setup_routes(self):
        """Configure les routes du serveur"""
        self.app.router.add_get('/', self.handle_index)
        self.app.router.add_get('/ws', self.handle_websocket)
        self.app.router.add_static('/static', Path(__file__).parent / 'static')
    
    async def handle_index(self, request):
        """Page d'accueil"""
        assets = [
            {'name': 'Joueur', 'icon': '🧑‍💻', 'type': 'player'},
            {'name': 'Ennemi', 'icon': '👾', 'type': 'enemy'},
            {'name': 'Pièce', 'icon': '⭐', 'type': 'coin'},
            {'name': 'Plateforme', 'icon': '🏗️', 'type': 'platform'},
            {'name': 'Arbre', 'icon': '🌳', 'type': 'decor'},
            {'name': 'Maison', 'icon': '🏠', 'type': 'building'},
            {'name': 'Cœur', 'icon': '❤️', 'type': 'health'},
            {'name': 'Épée', 'icon': '⚔️', 'type': 'weapon'},
            {'name': 'Portail', 'icon': '🌀', 'type': 'portal'},
            {'name': 'Clé', 'icon': '🔑', 'type': 'key'},
            {'name': 'Étoile', 'icon': '✨', 'type': 'powerup'},
            {'name': 'Bouclier', 'icon': '🛡️', 'type': 'shield'},
        ]
        
        default_script = '''# 🎮 PixelVerse Studio - Script Python
# Utilisez l'API pour créer votre jeu !

# Créer un joueur
player = api.create_player(400, 300)

# Créer des plateformes
api.create_platform(200, 500, 200, 30)
api.create_platform(500, 400, 200, 30)
api.create_platform(800, 300, 200, 30)

# Créer des pièces
for i in range(10):
    api.create_coin(100 + i * 80, 250)

# Créer des ennemis
for i in range(3):
    api.create_enemy(200 + i * 200, 300)

api.log("✅ Jeu initialisé !")

# Fonction appelée à chaque frame
def update(dt):
    # La physique et les collisions sont gérées automatiquement
    pass
'''
        
        html = self.html_template.render(
            assets=assets,
            default_script=default_script
        )
        return web.Response(text=html, content_type='text/html')
    
    async def handle_websocket(self, request):
        """Gestionnaire WebSocket"""
        ws = web.WebSocketResponse()
        await ws.prepare(request)
        self.clients.add(ws)
        
        try:
            async for msg in ws:
                if msg.type == WSMsgType.TEXT:
                    data = json.loads(msg.data)
                    await self.process_message(ws, data)
                elif msg.type == WSMsgType.ERROR:
                    print(f"WebSocket error: {ws.exception()}")
        finally:
            self.clients.discard(ws)
        
        return ws
    
    async def process_message(self, ws, data: dict):
        """Traite les messages du client"""
        msg_type = data.get('type')
        
        if msg_type == 'keydown':
            key = data.get('key', '')
            self.engine.current_scene.input.keys_pressed.add(key)
            self.engine.current_scene.input.keys_just_pressed.add(key)
            
        elif msg_type == 'keyup':
            key = data.get('key', '')
            self.engine.current_scene.input.keys_pressed.discard(key)
            self.engine.current_scene.input.keys_released.add(key)
            
        elif msg_type == 'run_script':
            code = data.get('code', '')
            self.script_manager.reset()
            success, message = self.script_manager.execute_script(code)
            await self.send_to_client(ws, {
                'type': 'log',
                'message': message
            })
            
        elif msg_type == 'stop':
            self.script_manager.reset()
            self.engine.current_scene.objects.clear()
            self.engine.current_scene.particles.clear()
            await self.send_to_client(ws, {
                'type': 'log',
                'message': '⏹️ Jeu arrêté'
            })
            
        elif msg_type == 'reset':
            self.engine.create_scene('main')
            self.script_manager.api.scene = self.engine.current_scene
            await self.send_to_client(ws, {
                'type': 'log',
                'message': '🔄 Scène réinitialisée'
            })
            
        elif msg_type == 'create_asset':
            asset = data.get('asset', {})
            asset_type = asset.get('type', 'object')
            
            if asset_type == 'player':
                self.script_manager.api.create_player(400, 300)
            elif asset_type == 'enemy':
                self.script_manager.api.create_enemy(400, 300)
            elif asset_type == 'coin':
                self.script_manager.api.create_coin(400, 300)
            elif asset_type == 'platform':
                self.script_manager.api.create_platform(400, 400)
            else:
                self.script_manager.api.create_object(
                    asset.get('name', 'Object'),
                    400, 300,
                    texture=asset.get('icon', '📦')
                )
            
            await self.send_to_client(ws, {
                'type': 'log',
                'message': f"✅ Créé: {asset.get('name', 'Objet')}"
            })
    
    async def send_to_client(self, ws, data: dict):
        """Envoie des données à un client spécifique"""
        try:
            await ws.send_json(data)
        except:
            self.clients.discard(ws)
    
    async def broadcast_state(self):
        """Diffuse l'état du jeu à tous les clients"""
        state = self.engine.get_state()
        for ws in self.clients.copy():
            try:
                await ws.send_json({
                    'type': 'state',
                    'state': state
                })
            except:
                self.clients.discard(ws)
    
    async def game_loop(self):
        """Boucle de jeu principale"""
        self.last_time = time.time()
        
        while True:
            current_time = time.time()
            dt = current_time - self.last_time
            self.last_time = current_time
            
            # Limiter le delta time
            dt = min(dt, 0.05)
            
            # Mettre à jour le moteur
            self.engine.update(dt)
            
            # Appeler les fonctions update des scripts
            self.script_manager.call_update(dt)
            
            # Diffuser l'état
            await self.broadcast_state()
            
            # Contrôler le taux de rafraîchissement (60 FPS)
            await asyncio.sleep(1/60)
    
    async def start(self):
        """Démarre le serveur"""
        print(f"""
╔══════════════════════════════════════════╗
║     🎮 PixelVerse Studio v2.0 🎮        ║
║     Moteur de Jeu 2.5D avec Python       ║
╠══════════════════════════════════════════╣
║  🌐 Serveur: http://{self.host}:{self.port}      ║
║  📡 WebSocket: ws://{self.host}:{self.port}/ws  ║
║  🐍 API Python prête                     ║
║  📱 Compatible Android/iOS               ║
╚══════════════════════════════════════════╝
        """)
        
        # Démarrer la boucle de jeu en arrière-plan
        asyncio.create_task(self.game_loop())
        
        # Démarrer le serveur web
        runner = web.AppRunner(self.app)
        await runner.setup()
        site = web.TCPSite(runner, self.host, self.port)
        await site.start()
        
        # Maintenir le serveur en vie
        await asyncio.Event().wait()

def main():
    """Point d'entrée principal"""
    import argparse
    
    parser = argparse.ArgumentParser(description='PixelVerse Studio - Moteur de Jeu 2.5D')
    parser.add_argument('--host', default='0.0.0.0', help='Adresse du serveur')
    parser.add_argument('--port', type=int, default=8080, help='Port du serveur')
    parser.add_argument('--script', type=str, help='Script Python à exécuter au démarrage')
    
    args = parser.parse_args()
    
    # Créer et démarrer le serveur
    server = GameServer(host=args.host, port=args.port)
    
    # Exécuter un script au démarrage si spécifié
    if args.script:
        with open(args.script, 'r') as f:
            code = f.read()
        server.script_manager.execute_script(code)
    
    # Lancer le serveur
    try:
        asyncio.run(server.start())
    except KeyboardInterrupt:
        print("\n👋 Au revoir !")

if __name__ == '__main__':
    main()
