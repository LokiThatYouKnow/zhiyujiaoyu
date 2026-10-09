import axios from "../utils/axios";

export const convertToTxt = async(request:any) =>{ 
  return (await axios.post('/convert/txt', request, {
    responseType: 'blob', // 重要：指定响应类型为blob，用于接收文件流
    timeout: 180000
  }))
}

export const convertToDoc = async(request:any) =>{ 
    return (await axios.post('/convert/doc', request, {
      responseType: 'blob', // 重要：指定响应类型为blob，用于接收文件流
      timeout: 180000
    }))
  }

  export const convertToDocx = async(request:any) =>{ 
    return (await axios.post('/convert/docx', request, {
      responseType: 'blob', // 重要：指定响应类型为blob，用于接收文件流
      timeout: 180000
    }))
  }
