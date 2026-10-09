export interface menu{
        id:number,
        name:string,
        icon:string,
        pid:number,
        path:string,
        component:string  
        childrenMenu?:menu[]
}      