import * as Icons from '@ant-design/icons-vue'

interface Module {
    [p:string]:any
}

export default {
    props:{
        icon:String
    },
    setup(props:{icon:string}){
        const im: Module  = Icons
        //@ts-ignore
        const tag = im[props.icon]
        return ()=><tag></tag>
    }
}