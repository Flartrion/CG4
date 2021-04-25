import java.awt.BorderLayout
import java.awt.GridLayout
import javax.swing.*

class MainWindow : JFrame() {
    private val windowDetection = WindowDetectionTest()
    private val pointEditButton = JButton("Отобразить")

    init {
        val inputPanel = JPanel()
        inputPanel.layout = BorderLayout()

        val pointInput = JPanel()
        pointInput.layout = BoxLayout(pointInput, BoxLayout.PAGE_AXIS)

        val sectionCount = JTextField("15")
        pointInput.add(JLabel("Количество отрезков: ", 0))
        pointInput.add(sectionCount)
        pointInput.add(pointEditButton)

        val rectangleAttributes = JPanel()
        rectangleAttributes.layout = GridLayout(7, 2)

        val height = JTextField("5")
        val width = JTextField("5")
        val positionX = JTextField("10")
        val positionY = JTextField("10")
        val buildRectangle = JButton("Построить")

        rectangleAttributes.add(JLabel("Длина: ", 0))
        rectangleAttributes.add(height)
        rectangleAttributes.add(JLabel("Ширина: ", 0))
        rectangleAttributes.add(width)
        rectangleAttributes.add(JLabel("Позиция", 0))
        rectangleAttributes.add(JLabel(" ", 0))
        rectangleAttributes.add(JLabel("X: ", 0))
        rectangleAttributes.add(positionX)
        rectangleAttributes.add(JLabel("Y: ", 0))
        rectangleAttributes.add(positionY)

        rectangleAttributes.add(buildRectangle)

        buildRectangle.addActionListener {
            windowDetection.windowHeight = height.text.toInt()
            windowDetection.windowWidth = width.text.toInt()
            windowDetection.windowPosX = positionX.text.toInt()
            windowDetection.windowPosY = positionY.text.toInt()
            repaint()
        }

        pointEditButton.addActionListener {
            windowDetection.generateRandomVertices(sectionCount.text.toInt() * 2)
            repaint()
        }

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

        this.add(inputPanel, BorderLayout.NORTH)
        this.defaultCloseOperation = EXIT_ON_CLOSE
        this.title = "Smooth criminal"
        this.isResizable = true
        this.add(windowDetection, BorderLayout.CENTER)
        this.pack()
        this.setLocationRelativeTo(null)
        this.isVisible = true
    }
}