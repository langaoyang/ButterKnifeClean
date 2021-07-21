
import java.io.File
import java.io.FileWriter

/**
 * 移除ButterKnife
 *
 *
 */
object ButterKnifeDealer {

    @JvmStatic
    fun main(args: Array<String>) {

//        val filePath = "/Users/lan/workspace/sweet-android/app/src"
        val filePath = "/Users/lan/Desktop/UserMeFragment.java"

        dealFile(File(filePath))


    }



    private val butterKnifeRegex = "^@BindView\\(\\s*\\S*\\)".toRegex()
    private val butterViewRegex = "^(public\\s+|private\\s+|protect\\s+)?[\\w<\\S\\s>]+[\\s]+[\\w]+\\s*;".toRegex()
    private val activityRegex = "[\\s\\S]*extends\\s+BaseActivity[\\s\\S]*".toRegex()
    private val fragmentRegex = "[\\s\\S]*extends\\s+BaseFragment[\\s\\S]*".toRegex()
    private val spaceRegex =  "\\s".toRegex()
    private val clickAreaRegex = "@OnClick\\s*\\(".toRegex()

    private fun dealFile(file: File) {


        if (file.isFile) {


            if (file.name.endsWith(".java")) {

                println(file.name)
                val beans = ArrayList<LineBean>()
                var  classType = 0;
                val allIndex = ArrayList<Int>()

                val allLines = file.readLines()

                var allText = file.readText()
                //直接根据内容
                for (i in allLines.indices) {

                    if (i < allLines.size - 1) {

                        val line = allLines[i]
                        when {
                            activityRegex.matches(line) -> {
                                if (classType != 0) {
                                    throw IllegalAccessException("包含多个类")
                                }
                                classType = 1
                            }
                            fragmentRegex.matches(line) -> {
                                if (classType != 0) {
                                    throw IllegalAccessException("包含多个类")
                                }

                                classType = 2

                            }
                            else -> {
                                // 获取regex


                            }
                        }





                        if (butterKnifeRegex.matches(line.trim())) {
//                            print("$line      =========")
                            var j = i + 1;

                            var nextLine: String
                            do {
                                nextLine = allLines[j]

                                if (nextLine.isBlank()) {
                                    j++
                                }


                            } while (nextLine.isBlank() && j <= allLines.size - 1)

                            if (butterViewRegex.matches(nextLine.trim())) {
//                                println("$line  ======   $nextLine")
                                beans.add(LineBean(line, nextLine, i, j))
                                allIndex.add(i)

                            } else {
                                println("$line        \n$nextLine          ")
                            }
                        }
                    }
                }









                if (beans.isNotEmpty()) {
                    val writer = FileWriter(file.absoluteFile)

                    val sb = StringBuilder()

                    for (i in allLines.indices) {
                        var s = allLines[i]

                        if (allIndex.contains(i)) {

                        }else{
                            sb.append(s).appendLine()
                        }

                    }
                    var lastIndexOf = sb.lastIndexOf("}")
                    sb.deleteCharAt(lastIndexOf)

                    if (classType == 1) {
                        sb.append("private void findViewsByButterKnifeDealer(){")
                        sb.appendLine()
                        for (bean in beans) {
                            var startLine = bean.startLine
                            var id  = startLine.substring(startLine.indexOf("(")+1,startLine.indexOf(")"))

                            var nextLine = bean.nextLine

                            sb.append("${nextLine.trim().replace(spaceRegex," ").split(" ").last().replace(";","")} = findViewById($id);")
                            sb.appendLine()

                        }
                        sb.append("}")

                    }else if (classType == 2) {
                        sb.append("private void findViewsByButterKnifeDealer(){")
                        sb.appendLine()
                        sb.append("View rootView = getView();")
                            .appendLine()
                            .append("if(rootView == null){").appendLine().append("return;").appendLine().append("}")

                        for (bean in beans) {
                            var startLine = bean.startLine
                            var id  = startLine.substring(startLine.indexOf("(")+1,startLine.indexOf(")"))

                            var nextLine = bean.nextLine

                            sb.append("${nextLine.trim().replace(spaceRegex," ").split(" ")[1].replace(";","")} = rootView.findViewById($id);")
                            sb.appendLine()

                        }
                        sb.append("}").appendLine()

                    }
                    sb.append("}")
                    writer.write(sb.toString())

                    writer.close()

//                    println(sb.toString())

                }




            }




        } else {

            val listFiles = file.listFiles()

            for (listFile in listFiles) {
                dealFile(listFile)
            }
        }
    }

    private fun getNextLine(i: Int, readLines: List<String>): String? {

        if (i + 1 < readLines.size - 1) {

            var s = readLines[i + 1]
            if (s.isBlank()) {


            }

        }
        return null


    }






}