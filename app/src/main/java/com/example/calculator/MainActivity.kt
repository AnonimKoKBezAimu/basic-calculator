package com.example.calculator

import android.os.Bundle
import android.view.Display
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.*


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            var line by remember { mutableStateOf("") }
            var resultLine by remember { mutableStateOf("") }

            fun precedence(operator: Char): Int = when (operator) {
                '+', '-' -> 1
                '*', '/' -> 2
                else -> 0
            }

            fun calculation(line: String): Double{
                var outputQueue = mutableListOf<String>()
                var operatorStack = mutableListOf<Char>()
                var stack = mutableListOf<Double>()

                //tokenizing
                var tmp: String =""
                for(i in line)  {
                    if(i != '+' && i != '-' && i != '*' && i != '/'){
                        tmp += i
                    }
                    else{
                        outputQueue += tmp
                        tmp = ""

                        while (operatorStack.isNotEmpty() &&
                            precedence(operatorStack.last()) >= precedence(i)) {
                            outputQueue += operatorStack.removeAt(operatorStack.size -1 ).toString()
                        }
                        operatorStack += i
                    }

                }
                if(tmp.isNotEmpty()) {outputQueue += tmp}

                while (operatorStack.isNotEmpty()) {
                    outputQueue += operatorStack.removeAt(operatorStack.size - 1).toString()
                }
                //counting
                for(i in outputQueue) {
                    if (i != "+" && i != "-" && i != "*" && i != "/") {
                        stack.add(i.toDouble())
                    }
                    else if (i == "+" || i == "-" || i == "*" || i == "/") {
                        val num1 = stack.removeAt(stack.size - 1).toDouble()
                        val num2 = stack.removeAt(stack.size - 1).toDouble()
                        val result = when (i) {
                            "+" -> num2 + num1
                            "-" -> num2 - num1
                            "*" -> num2 * num1
                            "/" -> num2 / num1
                            else -> error("unknown error")
                        }
                        stack.add(result)
                    }
                }
                return stack.last()
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {

                MyDisplay(modifier = Modifier
                    .fillMaxWidth()
                    .weight(.4f)
                    .background(Color.Black),
                    line,
                    resultLine
                )

                CalcKeyboard(
                    modifier = Modifier.weight(.6f),
                    onClick = {label ->
                        when (label) {
                            "C" -> {line = ""
                                    resultLine = ""
                            }
                            "=" -> {
                                val result = calculation(line)
                                line = if (result %1.0 == 0.0){
                                    result.toInt().toString()
                                } else{
                                    result.toString()
                                }
                                resultLine = ""
                            }
                            else -> {
                                line += label
                                try {
                                    val result = calculation(line)
                                    resultLine = if (result % 1.0 == 0.0) {
                                        result.toInt().toString()
                                } else {
                                    result.toString()
                                }
                                } catch (e: Exception) {
                                    //ignore invalid (like "2+")
                                    resultLine = line
                                }
                            }
                        }
                    }
                )
                Box(modifier = Modifier
                    .weight(0.07f)
                    .fillMaxWidth()
                    .background(color = Color.Black)
                )
            }
        }
    }
}


val labels = listOf(
    listOf("7","8","9","C"),
    listOf("4","5","6","="),
    listOf("1","2","3","/"),
    listOf("0","+","-","*"),
)

@Composable
fun CalcKeyboard(
    modifier: Modifier = Modifier,
    onClick: (String) -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxHeight()
            .background(Color.Black)
    )  {
        for(row in labels) {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            )  {
                for(label in row){

                    val buttonLabelColor = when (label){
                        "/", "*", "-", "+","C","=" -> Color.White
                        else -> Color.Black
                    }

                    val buttonBackgroundColor = when (label){
                        "C" -> Color(226, 68, 98)
                        "=" -> Color(177, 37, 234)
                        else -> Color(127, 82, 255)
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxSize()
                            .padding(5.dp)
                            .clickable { onClick(label) }
                            .background(
                                color = buttonBackgroundColor,
                                shape = RoundedCornerShape(10.dp)
                            )
                            .border(5.dp, color = Color.Transparent)
                            .clickable{ onClick(label)},
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            fontSize = 45.sp,
                            color = buttonLabelColor
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun MyDisplay(
    modifier: Modifier = Modifier,
    funLine: String,
    funResult: String
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 15.dp, vertical = 20.dp)
    ) {
        Box(
            contentAlignment = Alignment.BottomEnd,
            modifier = modifier
                .weight(2f)
                .fillMaxWidth()
        ) {
            Text(
                text = funLine,
                fontSize = 45.sp,
                textAlign = TextAlign.End,
                color = Color.White,
                modifier = Modifier.fillMaxWidth()
            )
        }
        Box(
            contentAlignment = Alignment.TopStart,
            modifier = modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            Text(
                text = funResult,
                fontSize = 30.sp,
                textAlign = TextAlign.End,
                color = Color.Gray,
                modifier = Modifier.fillMaxWidth().padding(end = 12.dp)
            )
        }
    }
}
