<template>


   <div>

    <p align="right" >
              <b-form-select v-model="formdata.exercicio" :options="formdata.exercicios"></b-form-select>
              &nbsp;   &nbsp;
              <b-form-select v-model="formdata.remessa" :options="formdata.remessas"></b-form-select>
             &nbsp;   &nbsp;   &nbsp;
            <b-button @click="search"  pill 
            variant="success" 
            size="sm">Pesquisar</b-button>
     </p>

     <b-table striped hover responsive 
         id="my-table"
        :items="tableData" 
        :fields="columns" 
        :per-page="perPage"
        :current-page="currentPage"
        aria-controls="my-table"
        small
        >
         <template #cell(index)="data">{{ data.index + 1 }}</template>
     </b-table>
    <b-pagination
      v-model="currentPage"
      :total-rows="rows"
      :per-page="perPage"
      aria-controls="my-table"
    ></b-pagination>
    <p class="mt-3">Current Page: {{ currentPage }}</p>
    </div>
</template>


<script>

import { mapActions, mapState } from 'vuex'

export default {

        data () {
          return {

                 

            perPage: 20,
            currentPage: 1,
            items:[],
            columns:[  
               {
                        key:'index',
                        label:'Indice',
                        formatter: 'index'
                      },
                      {
                        key: 'nomeEntidade',
                        label:'Entidade',
                        sortable: true
                       // formatter: 'todasMaiusculas'
                      },
                       {
                        key: 'cnpj',
                         label:'Cnpj',
                        sortable: false
                      },
                      {
                         key: 'exercicio',
                         label:'exercicio',
                         sortable: false
                      },
                       {
                         key: 'remessa',
                         label:'remessa',
                         sortable: false
                      },
                       {
                        key: 'dataEntrega',
                         label:'Data Entrega',
                        sortable: false
                      },
                      {
                        key: 'dataAssinatura',
                         label:'Data Assinatura',
                        sortable: true
                      }
                      
          ],
         
            formdata:{
               exercicio: 2021,
               exercicios: [
                            { value: '2021', text: '2021' }
                          ],
               remessa: 1,
               remessas: [
                            { value: '1', text: '1' },
                            { value: '2', text: '2' },
                            { value: '3', text: '3' },
                            { value: '4', text: '4' },
                            { value: '5', text: '5' }
                          ]
            },
           // modalShow:false,
           // editedIndex: -1
          }
        },
       
       mounted(){

            this.ActionFind()
        
        },

        computed:{

                  rows() {
                  return this.tableData.length
                  },
                  ...mapState('remessas', ['tableData'])
        },

        methods: {
                   ...mapActions('remessas', ['ActionFind']),
                   ...mapActions('remessas', ['ActionFindByRemessa']),

                async search() {
                
//console.log(this.formdata.exercicio+"-----"+this.formdata.remessa)
//this.tableData=[],
                                await this.ActionFindByRemessa(this.formdata)
                }

          }
  }
</script>
<style >

</style>