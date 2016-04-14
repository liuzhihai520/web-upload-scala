package controllers

import java.io.{InputStreamReader, BufferedReader, FileInputStream, File}

import org.apache.commons.io.FilenameUtils
import org.joda.time.DateTime
import play.api.libs.Codecs
import play.api.libs.json.Json
import play.api.mvc._

import scala.sys.process.Process

object Application extends Controller {

    //路劲
    val PATH = "/home/resource/"
    //图片规格
    val avatarSize = Array("70x70","100x100","120x120")
    val workSize = Array("60x60","90x90","120x120")
    //参数
    val K_USER_ID = "userId"   //人员编号
	val K_UUID_ID = "uuid"   //人员编号
    val K_FILE_UPLOAD = "Filedata" //文件key
    val K_FILE_TYPE = "filetype"  //文件类型
    val K_PIC_TYPE = "pictype"    //图片类型比如web/mobile
    val K_CORP_ID = "corpId" //企业编号
    //哈希值
    val HEXES = "0123456789ABCDEF"

    //返回值
    val mission_param = Ok(Json.stringify(Json.parse( """{"status" : 1, "message" : "missing params"}"""))).as("text/json").withHeaders((CACHE_CONTROL, "no-cache"))

    val mission_file = Ok(Json.stringify(Json.parse( """{"status" : 1, "message" : "missing file"}"""))).as("text/json").withHeaders((CACHE_CONTROL, "no-cache"))

    val success = Ok(Json.stringify(Json.parse( """{"status" : 0, "message" : "success"}"""))).as("text/json").withHeaders((CACHE_CONTROL, "no-cache"))

    //上传图片
    def upload = Action(parse.multipartFormData) {
        request =>
            var result = mission_param
            val formData = request.body.asFormUrlEncoded
            val corpId = formData.get(K_CORP_ID).map(_.head).getOrElse("")
			val uuid = formData.get(K_UUID_ID).map(_.head).getOrElse("")
            val userId = formData.get(K_USER_ID).map(_.head).getOrElse("")
            val filetype = formData.get(K_FILE_TYPE).map(_.head).getOrElse("0")
            val pictype = formData.get(K_PIC_TYPE).map(_.head).getOrElse("0").toInt
            filetype match {
                case "avatar" => {
                    if (corpId.equals("")) {
                        Ok(Json.stringify(Json.parse( """{"status" : 1, "message" : "missing params cropId"}"""))).as("text/json").withHeaders((CACHE_CONTROL, "no-cache"))
                    }else if(userId.equals("")){
                        Ok(Json.stringify(Json.parse( """{"status" : 2, "message" : "missing params userId"}"""))).as("text/json").withHeaders((CACHE_CONTROL, "no-cache"))
                    }else{
                        request.body.file(K_FILE_UPLOAD).map { f =>
                            val hash = fileHash(f.ref.file).toLowerCase()
                            f.ref.moveTo(new File(createPath(s"avatar/$corpId/$userId/$userId.jpg")), replace = true)

                            workSize.foreach({ f =>
                                val from = createPath(s"avatar/$corpId/$userId/$userId.jpg")
                                val size = f.replace('x', '/')
                                val to = createPath(s"avatar/$corpId/$userId/$size/$userId.jpg")
                                thumbnails(from, to, f)
                            })

                            result = Ok(Json.stringify(Json.parse(s"""{"status" : 0, "message" :"success","url":"avatar/$corpId/$userId/120/120/$userId.jpg"}"""))).withHeaders((CACHE_CONTROL, "no-cache"))
                        } getOrElse {
                            result = mission_file
                        }
                    }
                }
                case "work" => {
                    request.body.file(K_FILE_UPLOAD).map { f =>
                        //创建日期
                        val date = new DateTime().toString("yyyyMMdd")
                        val hash = fileHash(f.ref.file).toLowerCase()
                        f.ref.moveTo(new File(createPath(s"work/$date/$hash.jpg")), replace = true)

                        workSize.foreach({ f =>
                            val from = createPath(s"work/$date/$hash.jpg")
                            val size = f.replace('x', '/')
                            val to = createPath(s"work/$date/$size/$hash.jpg")
                            thumbnails(from, to, f)
                        })

                        result = Ok(Json.stringify(Json.parse(s"""{"status" : 0, "message" :"success","url":"work/$date/$hash.jpg"}"""))).withHeaders((CACHE_CONTROL, "no-cache"))
                    } getOrElse {
                        result = mission_file
                    }
                }
                case "leave" => {
                    if (corpId.equals("")) {
                        Ok(Json.stringify(Json.parse( """{"status" : 1, "message" : "missing params cropId"}"""))).as("text/json").withHeaders((CACHE_CONTROL, "no-cache"))
                    }else if(userId.equals("")){
                        Ok(Json.stringify(Json.parse( """{"status" : 2, "message" : "missing params userId"}"""))).as("text/json").withHeaders((CACHE_CONTROL, "no-cache"))
                    }else{
                        request.body.file(K_FILE_UPLOAD).map { f =>
                            //创建日期
                            val date = new DateTime().toString("yyyyMMdd")
                            val hash = fileHash(f.ref.file).toLowerCase()
                            f.ref.moveTo(new File(createPath(s"leave/$date/$corpId/$userId/$hash.jpg")), replace = true)

                            workSize.foreach({ f =>
                                val from = createPath(s"leave/$date/$corpId/$userId/$hash.jpg")
                                val size = f.replace('x', '/')
                                val to = createPath(s"leave/$date/$corpId/$userId/$size/$hash.jpg")
                                thumbnails(from, to, f)
                            })

                            result = Ok(Json.stringify(Json.parse(s"""{"status" : 0, "message" :"success","url":"leave/$date/$corpId/$userId/$hash.jpg"}"""))).withHeaders((CACHE_CONTROL, "no-cache"))
                        } getOrElse {
                            result = mission_file
                        }
                    }
                }
				case "suite" => {
					request.body.file(K_FILE_UPLOAD).map { f =>
						//创建日期
						val date = new DateTime().toString("yyyyMMdd")
						val hash = fileHash(f.ref.file).toLowerCase()
						f.ref.moveTo(new File(createPath(s"suite/$date/$hash.jpg")), replace = true)

						workSize.foreach({ f =>
							val from = createPath(s"suite/$date/$hash.jpg")
							val size = f.replace('x', '/')
							val to = createPath(s"suite/$date/$size/$hash.jpg")
							thumbnails(from, to, f)
						})

						result = Ok(Json.stringify(Json.parse(s"""{"status" : 0, "message" :"success","url":"suite/$date/120/120/$hash.jpg"}"""))).withHeaders((CACHE_CONTROL, "no-cache"))
					} getOrElse {
						result = mission_file
					}
                    
                }
				case "burse" => {
					if (corpId.equals("")) {
                        Ok(Json.stringify(Json.parse( """{"status" : 1, "message" : "missing params corpId"}"""))).as("text/json").withHeaders((CACHE_CONTROL, "no-cache"))
                    }else if(userId.equals("")){
                        Ok(Json.stringify(Json.parse( """{"status" : 2, "message" : "missing params userId"}"""))).as("text/json").withHeaders((CACHE_CONTROL, "no-cache"))
                    }else if(uuid.equals("")){
						Ok(Json.stringify(Json.parse( """{"status" : 3, "message" : "missing params uuid"}"""))).as("text/json").withHeaders((CACHE_CONTROL, "no-cache"))
					}else{
						request.body.file(K_FILE_UPLOAD).map { f =>
							//创建日期
							val date = new DateTime().toString("yyyyMMdd")
							val hash = fileHash(f.ref.file).toLowerCase()
							f.ref.moveTo(new File(createPath(s"burse/$corpId/$userId/$date/$hash.jpg")), replace = true)

							workSize.foreach({ f =>
								val from = createPath(s"burse/$corpId/$userId/$date/$hash.jpg")
								val size = f.replace('x', '/')
								val to = createPath(s"burse/$corpId/$userId/$date/$size/$hash.jpg")
								thumbnails(from, to, f)
							})

							result = Ok(Json.stringify(Json.parse(s"""{"status" : 0,"uuid":"$uuid","message" :"success","url":"burse/$corpId/$userId/$date/120/120/$hash.jpg"}"""))).withHeaders((CACHE_CONTROL, "no-cache"))
						} getOrElse {
							result = mission_file
						}
					}
                }
            }
            result
    }

    //图片裁剪
    def cropper(filename: String, filetype: String, x: Int, y: Int, size: String) = Action { request =>
        var result = success
        if (!filename.equals("") && !x.equals("") && !y.equals("") && !size.equals("")) {
            filetype match {
                case "item" => {
                    val newFile = Codecs.md5(filename.getBytes)
                    val originFile = createPath(filename)
                    val cropFile = createPath(s"project/img/$newFile.jpg")
                    Process(s"""convert -strip +profile "*" -quality 90 $originFile[0] -crop $size+$x+$y +repage $cropFile""").!
                    result = Ok(Json.stringify(Json.parse(s"""{"status" : 0, "message" :"success","url":"project/img/$newFile.jpg"}"""))).withHeaders((CACHE_CONTROL, "no-cache"))
                }
            }
        } else {
            result = mission_param
        }
        result
    }

    //创建路劲
    def createPath(filePath: String): String = {
        val path = FilenameUtils.getPath(filePath)
        val name = FilenameUtils.getBaseName(filePath)
        val ext = FilenameUtils.getExtension(filePath)

        if (isWindows) {
            new File(s"$PATH$path").mkdirs()
        } else {
            Process(s"mkdir -p $PATH$path").!
        }

        PATH + path + name + "." + ext
    }

    //文件哈希
    def fileHash(file: File): String = {
        val input = new StringBuffer()
        val stream = new FileInputStream(file)
        val buffer = new BufferedReader(new InputStreamReader(stream))
        while (buffer.ready()) {
            input.append(buffer.readLine() + "\n")
        }
        buffer.close()
        byteAryToHexStr(getHash(input.toString(), "MD5"))
    }

    def byteAryToHexStr(input: Array[Byte]): String = {
        val hex = new StringBuffer(2 * input.length)
        input.foreach({ f =>
            hex.append(HEXES.charAt((f & 0xF0) >> 4)).append(HEXES.charAt((f & 0x0F)))
        })
        return hex.toString()
    }

    def getHash(toHash: String, technique: String): Array[Byte] = {
        val parse = java.security.MessageDigest.getInstance(technique)
        parse.reset()
        parse.update(toHash.getBytes())
        return parse.digest()
    }


    def isWindows(): Boolean = {
        if (System.getProperties().getProperty("os.name").toUpperCase().indexOf("WINDOWS") != -1) {
            true;
        } else {
            false
        }
    }

    //删除文件
    def deleteFile(filePath: String) = {
        if (isWindows) {
            new File(filePath).delete()
        } else {
            (Process("rm -rf " + filePath)).!
        }
    }

    //按比例生成图片文件
    def thumbnails(from: String, to: String, size: String) = {
        Process( s"""convert -strip +profile "*" -quality 90 -resize $size $from[0] $to""").!
    }

    //图片裁剪
    def cropperWidth(from: String, to: String, maxWidth: String) = {
        Process( s"""convert -strip +profile "*" -quality 90 -resize $maxWidth $from[0] $to""").!
    }

    //图片缩放
    def zoomThumb(from: String, to: String, size: String) = {
        Process( s"""convert -sample $size $from[0] $to""").!
    }

    def crossdomain = Action {
        implicit viewData =>
            Ok("<cross-domain-policy><site-control permitted-cross-domain-policies=\"all\"/><allow-access-from domain=\"*\"/><allow-http-request-headers-from domain=\"*\" headers=\"*\"/></cross-domain-policy>").as("text/xml")
    }

    def test = Action {
        implicit request =>
            Ok(views.html.index())
    }
}
