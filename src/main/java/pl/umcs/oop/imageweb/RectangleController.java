package pl.umcs.oop.imageweb;

import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/rectangle")
public class RectangleController {
    private List<Rectangle> rectangles = new ArrayList<>();


//    public RectangleController() {
//        rectangles.add(new Rectangle(10, 20, 30, 40, "red"));
//        rectangles.add(new Rectangle(50, 60, 70, 80, "blue"));
//    }

    @GetMapping("/rect")
    public String init() {
        rectangles.add(new Rectangle(10, 20, 30, 40, "red"));
        rectangles.add(new Rectangle(50, 60, 70, 80, "blue"));
        rectangles.add(new Rectangle(200, 0, 160, 200, "green"));
        return "<h1>Initialized</h1>";
    }

//    @GetMapping
//    public Rectangle getRectangle() {
//        return new Rectangle( 10,20,30,40,"black");
//    }

    @PostMapping("/add")
    public void addRectangle( @RequestBody Rectangle rectangle ) {
        rectangles.add(rectangle);
    }
    @GetMapping("/list")
    public List<Rectangle> getRectangles() {
        return rectangles;
    }

    @GetMapping("/svg")
    public String toSVG(){
        StringBuilder result = new StringBuilder("<svg width=\"800\" height=\"500\" xmlns=\"http://www.w3.org/2000/svg\">\n");
        StringBuilder temp = new StringBuilder();
        for (Rectangle rectangle : rectangles) {
            temp.append("<rect width=\"").append(rectangle.getWidth()).append("\" height=\"").append(rectangle.getHeight()).append("\" x=\"").append(rectangle.getX()).append("\" y=\"").append(rectangle.getY()).append("\" fill=\"").append(rectangle.getColor()).append("\" />\n\"");
            result.append(temp.toString());
        }
        return result.append("</svg>").toString();
    }

    @GetMapping("/get")
    public Rectangle getRectangle(@RequestParam int index){
        return rectangles.get(index);
    }

    @PutMapping("/put")
    public void putRectangle( @RequestParam int index, @RequestBody Rectangle rectangle ) {
        rectangles.set(index, rectangle);
    }
    @DeleteMapping("/delete")
    public void deleteRectangle( @RequestParam int index ) {
        rectangles.remove(index);
    }


}
