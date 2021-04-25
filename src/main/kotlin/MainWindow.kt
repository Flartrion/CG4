import java.awt.BorderLayout
import java.awt.GridLayout
import javax.swing.*

class MainWindow : JFrame() {
    private val pointData = DefaultListModel<Vertex>()
    private val bezierSurface = WindowDetectionTest()
    private val pointEditButton = JButton("Отобразить")

    init {
        val inputPanel = JPanel()
        inputPanel.layout = BorderLayout()

        val pointInput = JPanel()
        pointInput.layout = BoxLayout(pointInput, BoxLayout.PAGE_AXIS)

        pointData.addElement(Vertex(-150.0, 0.0, 150.0))
        pointData.addElement(Vertex(-150.0, 5.0, 50.0))
        pointData.addElement(Vertex(-150.0, 5.0, -50.0))
        pointData.addElement(Vertex(-150.0, 0.0, -150.0))

        pointData.addElement(Vertex(-50.0, 50.0, 150.0))
        pointData.addElement(Vertex(-50.0, 50.0, 50.0))
        pointData.addElement(Vertex(-50.0, 50.0, -50.0))
        pointData.addElement(Vertex(-50.0, 50.0, -150.0))

        pointData.addElement(Vertex(50.0, 50.0, 150.0))
        pointData.addElement(Vertex(50.0, 50.0, 50.0))
        pointData.addElement(Vertex(50.0, 50.0, -50.0))
        pointData.addElement(Vertex(50.0, 50.0, -150.0))

        pointData.addElement(Vertex(150.0, 0.0, 150.0))
        pointData.addElement(Vertex(150.0, 50.0, 50.0))
        pointData.addElement(Vertex(150.0, 50.0, -50.0))
        pointData.addElement(Vertex(150.0, 0.0, -150.0))


        pointInput.add(JLabel("Количество отрезков: ", 0))
        pointInput.add(JTextField("15"))
        pointInput.add(pointEditButton)

        val rectangleAttributes = JPanel()
        rectangleAttributes.layout = GridLayout(7, 2)

        val length = JTextField("5")
        val width = JTextField("5")
        val positionX = JTextField("10")
        val positionY = JTextField("10 ")
        val positionZ = JTextField("10")
        val buildRectangle = JButton("Построить")

        buildRectangle.addActionListener {
            bezierSurface.rotateOnX(length.text.toDouble())
            repaint()
        }

        rectangleAttributes.add(JLabel("Длина: ", 0))
        rectangleAttributes.add(length)
        rectangleAttributes.add(JLabel("Ширина: ", 0))
        rectangleAttributes.add(width)
        rectangleAttributes.add(JLabel("Позиция", 0))
        rectangleAttributes.add(JLabel(" ", 0))
        rectangleAttributes.add(JLabel("X: ", 0))
        rectangleAttributes.add(positionX)
        rectangleAttributes.add(JLabel("Y: ", 0))
        rectangleAttributes.add(positionY)
        rectangleAttributes.add(JLabel("Z: ", 0))
        rectangleAttributes.add(positionZ)

        rectangleAttributes.add(buildRectangle)

        val topLabels = JPanel()
        topLabels.layout = GridLayout(1, 2)
        topLabels.add(JLabel("Отрезки", 0))
        topLabels.add(JLabel("Окно", 0))

        val inputZone = JPanel()
        inputZone.layout = GridLayout(1, 2)
        inputZone.add(pointInput)
        inputZone.add(rectangleAttributes)

        inputPanel.add(topLabels, BorderLayout.NORTH)
        inputPanel.add(inputZone, BorderLayout.SOUTH)

        for (i in 0 until pointData.size()) {
           // bezierSurface.points.add(pointData[i])
        }
        //bezierSurface.calculatePoints()

        this.add(inputPanel, BorderLayout.NORTH)
        this.defaultCloseOperation = EXIT_ON_CLOSE
        this.title = "Smooth criminal"
        this.isResizable = true
        this.add(bezierSurface, BorderLayout.CENTER)
        this.pack()
        this.setLocationRelativeTo(null)
        this.isVisible = true
    }
}