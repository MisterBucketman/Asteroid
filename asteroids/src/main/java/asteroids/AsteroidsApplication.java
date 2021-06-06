package asteroids;
 
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Text;
import javafx.stage.Stage;
 
public class AsteroidsApplication extends Application{
    
    public static int WIDTH = 300;
    public static int HEIGHT = 200;
    
    @Override
    public void start(Stage window) {
        Pane pane = new Pane();
        pane.setPrefSize(WIDTH, HEIGHT);
        Text text = new Text(10, 20, "Points: 0");
        pane.getChildren().add(text);
        
        AtomicInteger points = new AtomicInteger();
        
        Ship ship = new Ship(WIDTH/2, HEIGHT/2);
        
        List<Asteroid> asteroidList = new ArrayList<>();
        for(int i = 0; i<5; i++) {
            Random rand = new Random();
            Asteroid asteroid = new Asteroid(rand.nextInt(100), rand.nextInt(100));
            asteroidList.add(asteroid);
        }
        
        List<Projectile> projectileList = new ArrayList<>();
//        Asteroid asteroid1 = new Asteroid(50, 50);
//        asteroid1.turnRight();
//        asteroid1.turnRight();
//        asteroid1.accelerate();
//        asteroid1.accelerate();
//        ship.setTranslateX(300);
//        ship.setTranslateY(200);
//        ship.setRotate(30);
        
        pane.getChildren().add(ship.getCharacter());
        asteroidList.forEach(asteroid -> pane.getChildren().add(asteroid.getCharacter()));
        
        Scene scene = new Scene(pane);
        
        Map<KeyCode, Boolean> pressedKeys = new HashMap<>();
        
        scene.setOnKeyPressed(event -> {
            pressedKeys.put(event.getCode(), Boolean.TRUE);
        });
        
        scene.setOnKeyReleased(event -> {
           pressedKeys.put(event.getCode(), Boolean.FALSE); 
        });
        
        new AnimationTimer() {
            
            @Override
            public void handle(long now) {
                //text.setText("Points: " + points.incrementAndGet());
                
                if(pressedKeys.getOrDefault(KeyCode.LEFT, false)) {
                    ship.turnLeft();
                }
                if(pressedKeys.getOrDefault(KeyCode.RIGHT, false)) {
                    ship.turnRight();
                }
                if(pressedKeys.getOrDefault(KeyCode.UP, false)) {
                    ship.accelerate();
                }
                if(pressedKeys.getOrDefault(KeyCode.SPACE, false) && projectileList.size() < 3 ) {
                    Projectile projectile = new Projectile((int) ship.getCharacter().getTranslateX(), (int) ship.getCharacter().getTranslateY());
                    projectile.getCharacter().setRotate(ship.getCharacter().getRotate());
                    projectileList.add(projectile);
                    
                    projectile.accelerate();
                    projectile.setMovement(projectile.getMovement().normalize().multiply(3));
                    
                    pane.getChildren().add(projectile.getCharacter());
                }
                
                ship.move();
                asteroidList.forEach(asteroid -> asteroid.move());
                projectileList.forEach(projectile -> projectile.move());
                
                projectileList.forEach(projectile -> {
                    asteroidList.forEach(asteroid -> {
                        if(projectile.collide(asteroid)) {
                            projectile.setAlive(false);
                            asteroid.setAlive(false);
                        }
                    });
                    
                    if(!projectile.isAlive()) {
                        text.setText("Points: " + points.addAndGet(1000));
                    }
                });
                
                projectileList.stream()
                        .filter(projectile -> !projectile.isAlive()) 
                        .forEach(projectile -> pane.getChildren().remove(projectile.getCharacter()));
                projectileList.removeAll(projectileList.stream()
                        .filter(projectile -> !projectile.isAlive())
                        .collect(Collectors.toList()));
                
                asteroidList.stream()
                        .filter(asteroid -> !asteroid.isAlive())
                        .forEach(asteroid -> pane.getChildren().remove(asteroid.getCharacter()));
                asteroidList.removeAll(asteroidList.stream()
                        .filter(asteroid -> !asteroid.isAlive())
                        .collect(Collectors.toList()));
                
                asteroidList.forEach(asteroid -> {
                    if(ship.collide(asteroid)) {
                        stop();
                    } 
                });
                
                if(Math.random() < 0.005) {
                    Asteroid asteroid = new Asteroid(WIDTH, HEIGHT);
                    if(!asteroid.collide(ship)) {
                        asteroidList.add(asteroid);
                        pane.getChildren().add(asteroid.getCharacter());
                    }
                }
                
            }
        }.start();
        
        window.setTitle("Asteroids!");
        window.setScene(scene);
        window.show();
        
    }
    
    public static void main(String[] args) {
        launch(AsteroidsApplication.class);
    }

    public static int partsCompleted() {
        // State how many parts you have completed using the return value of this method
        return 4;
    }
}