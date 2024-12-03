<template>
  <div class="overflow-auto">
    <b-card no-body class="mb-2 mt-0" >
      <b-card-header header-tag="nav">
        <b-nav card-header tabs>
          <b-nav-item active>Fila de Processamento - Em andamento</b-nav-item>
        </b-nav>
      </b-card-header>
      <b-card-body v-if="this.fila === 0">
        {{ "Sem processos" }}
      </b-card-body>
      <b-card-body v-else>
        <b-table striped hover responsive sticky-header="450px" id="table" :items="fila" :fields="items2"
          :per-page="perPage" :current-page="currentPage" aria-controls="table" small>
          <!-- <template #table-busy>
            <div class="text-center text-danger my-2">
              <b-spinner class="align-middle"></b-spinner>
              <strong>Loading...</strong>
            </div>
          </template> -->
        </b-table>
        <p v-if="fila.length == 0" class="text-danger">Sem processos!</p>
      </b-card-body>
    </b-card>
    <b-card no-body>
      <b-card-header header-tag="nav">
        <b-nav card-header tabs>
          <b-nav-item active>Fila de Processamento - Finalizados</b-nav-item>
        </b-nav>
      </b-card-header>
      <b-row>
        &nbsp;&nbsp;&nbsp;
        <b-col>
          <p align="left" class="label-spacing">
            <b-form-group label="Unidade Gestora:	">
              <b-form-input style="height: auto;" list="unidadeGestora" required v-model="filterform" name="unidadeGestora"
                placeholder="Pesquise aqui...">
              </b-form-input>

              <b-form-datalist id="unidadeGestora">
                <option v-for="(item, index) in unidades" v-bind:key="index.id">
                  {{ item.nome }}
                </option>
              </b-form-datalist>
            </b-form-group>
          </p>
        </b-col>
        <b-col  cols="auto">
          <p align="left" >
            <b-row>
              <b-col cols="auto">
                <b-form-group style="height: auto; margin-left: 10px;" class="label-spacing" label="Exercício:">
                  <!-- @charge="filtraStatus"-->
                  <b-form-select class="select-selected " v-model="formdata.exercicio" :options="formdata.exercicios">
                  </b-form-select>
                </b-form-group>
              </b-col>
              &nbsp;
              <b-col cols="auto">
                <b-form-group style="height: auto;  margin-left: 10px;" class="label-spacing" label="Remessa:">
                  <!-- @charge="filtraStatus"-->
                  <b-form-select class="select-selected spacing" v-model="formdata.remessa" :options="formdata.remessas">
                  </b-form-select>
                </b-form-group>
              </b-col>
            </b-row>
          </p>
          <p align="right" class="label-spacing">
            <b-button pill variant="warning spacing" @click="resetFilter" size="sm">
              Limpar
            </b-button>
            <b-button @click="pesquisarRemesssa(formdata.exercicio, formdata.remessa)" pill variant="success" size="sm">
              Pesquisar
              </b-button>
             &nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp;
          </p>
        </b-col>
      </b-row>

      <b-card-body>
        <b-table striped hover responsive sticky-header="450px" :busy="isBusy" id="my-table" :filter="filter"
          :items="processos" :filter-included-fields="['nome']" :fields="items" :per-page="perPage"
          :current-page="currentPage" aria-controls="my-table" :tbody-tr-class="rowClass" small>
          <template #table-busy>
            <div class="text-center text-danger my-2">
              <b-spinner class="align-middle"></b-spinner>
              <strong>Loading...</strong>
            </div>
          </template>

          <template #cell(status)="data">
            <b-icon v-if="statusIcon(data.value) == 'x-circle'" class="h6 mb-1" :icon="statusIcon(data.value)"
              variant="danger" cursor="pointer" title="Assinaturas" @click="info(data.item, data.index, $event.target)"
              pill size="sm">
            </b-icon>
            <b-icon v-else class="h6 mb-1" :icon="statusIcon(data.value)" variant="success" cursor="pointer"
              title="Assinaturas" @click="info(data.item, data.index, $event.target)" pill size="sm">
            </b-icon>
          </template>
        </b-table>
        <div v-show="FilterSize < 1 && isBusy==false" class=" text-center font-weight-bold" style="font-size: 1.3em;">
          <strong>
            não contém registros
          </strong>
        </div>
        <b-row>
          <b-col cols="auto">
            <b-icon class="h6 mb-1" style="display: inline-block;" icon="check-square" variant="success"> </b-icon>
            &nbsp; &nbsp;Enviado &nbsp; &nbsp; 
          </b-col>
          <b-col cols="auto">
            <b-icon class="h6 mb-1" style="display: inline-block;" icon="x-circle" variant="danger"> </b-icon>
            &nbsp; &nbsp;Cancelado
          </b-col>
          <b-col>
            <div class="align-right  font-weight-bold spacing" style="font-size: 1.3em;">
              <strong>
                {{ FilterSize }} Registro(s)
              </strong>
            </div>
          </b-col>
        </b-row>
      </b-card-body>
    </b-card>
    <!-- <b-pagination
      v-model="currentPage"
      :total-rows="rows"
      :per-page="perPage"
      aria-controls="my-table"
    ></b-pagination>
     -->
  </div>
</template>

<script>
import { api } from "@/plugins/axios";

export default {
  data() {
    return {
      FilterSize: '',
      unidades: [],
      aprovado: "aprovado",
      isBusy: true,
      perPage: 5000,
      currentPage: 1,
      filter: "",
      filterform: "",
      processos: [],
      formdata: {
        exercicio: '',
        exercicios: [],
        remessa: '',
        remessas: [],
      },
      fila: [],
      items: [
        {
          key: "nome",
          label: "Unidade Gestora",
          sortable: false,
          tdClass: "fonteLinhaLeft",
        },
        {
          key: "exercicio",
          label: "Exercício",
          thStyle: { textAlign: "center"},
          sortable: true,
          tdClass: "fonteLinha",
        },
        {
          key: "remessa",
          label: "Remessa",
          thStyle: { textAlign: "center"},
          sortable: true,
          tdClass: "fonteLinha",
        },
        {
          key: "dataEnvio",
          label: "Data Envio",
          formatter: "formatarData",
          thStyle: { textAlign: "center"},
          sortable: false,
          tdClass: "fonteLinha",
        },
        {
          key: "dataProcessamento",
          label: "Data Procesamento",
          formatter: "formatarData",
          thStyle: { textAlign: "center"},
          sortable: true,
          tdClass: "fonteLinha",
        },
        {
          key: "status",
          label: "Status",
          thStyle: { textAlign: "center"},
          sortable: false,
          tdClass: "fonteLinha",
        },
      ],
      items2: [
        {
          key: "nome",
          label: "Unidade Gestora",
          sortable: false,
          tdClass: "fonteLinhaLeft",
        },
        {
          key: "exercicio",
          label: "Exercício",
          thStyle: { textAlign: "center"},
          sortable: false,
          tdClass: "fonteLinha",
        },
        {
          key: "remessa",
          label: "Remessa",
          thStyle: { textAlign: "center"},
          sortable: false,
          tdClass: "fonteLinha",
        },
        {
          key: "dataEnvio",
          label: "Data Envio",
          thStyle: { textAlign: "center"},
          sortable: false,
          formatter: "formatarData",
          tdClass: "fonteLinha",
        },
        {
          key: "posicao",
          label: "Posição",
          thStyle: { textAlign: "center"},
          sortable: false,
          tdClass: "fonteLinha",
        },
        {
          key: "status",
          label: "Status",
          thStyle: { textAlign: "center"},
          sortable: false,
          tdClass: "fonteLinha",
        },
      ],
    };
  },
  mounted() {
    this.isBusy = false;
    this.pesquisarExercicios();
    this.pesquisarExercicioVigente();
    this.readForms();
    this.findAllUnidadeGestora().then((resp) => {
      this.unidades = resp;
    });
    //  setTimeout(() =>{// aguarda com spinner antes da pesquisa aparecer na pesquisa inicial
    //               this.isBusy = false
    //               }, 1.0*1000)
    // // this.isBusy = false
    // // this.pesquisar()
    // setInterval(this.readForms, 1000);
  },
  methods: {

    filtroInicial(){
      // this.filterSize();
      this.processos = [];
      this.formdata.exercicio = this.pesquisarExercicioVigente();
      this.formdata.exercicios = this.pesquisarExercicios();
      this.filterform = "";
      this.filter = "";
    },

    resetFilter(){
      this.formdata = { ...this.filtroInicial()}
    },

    async pesquisarExercicioVigente() { // vai buscar o exercicio de está em vigencia
      //this.isBusy = !this.isBusy; //loading
      await api.get("/exercicio/exercicioVigente").then((resp) => {
        //commit('getUnidades', resp.data)
        this.formdata.exercicio = resp.data[0];
        this.pesquisarRemessaVigente(resp.data);
        this.pesquisarRemessas(resp.data);
      })
    },

    pesquisarRemessaVigente(exercicio) { // vai buscar o remessa de está em vigencia
      api.get("/remessa/remessaVigente/" + exercicio).then((resp) => {
        // commit('getUnidades', resp.data)
        this.formdata.remessa = resp.data[0];
        this.pesquisarRemesssa(exercicio, resp.data);
      })
    },

    pesquisarExercicios() {
      api.get("/exercicio").then((resp) => {
        this.formdata.exercicios = resp.data
          .filter(p => p > 2020)
          .map((p) => {
            return {
              value: p,
              text: "" + p,
            };
          });
      });
    },

    pesquisarRemessas() {
      api.get("/remessa/" + this.formdata.exercicio).then((resp) => {
        this.formdata.remessas = resp.data.map((p) => {
          return {
            value: p,
            text: "" + p,
          };
        });
      });
    },

    findAllUnidadeGestora: () =>
      api.get("/unidadeGestora/todos").then((resp) => {
        //commit('getUnidades', resp.data)
        return resp.data;
      }),

      pesquisarRemesssa(exercicio, remessa) {
        this.isBusy = !this.isBusy;
        api.get("filaProcessamento/processos/" + exercicio + "/" + remessa).then((resp) => {
          this.processos = resp.data;
          this.filterSize();
          this.isBusy = !this.isBusy;
        });
      this.filter = this.filterform;
    },
    FindAll() {
      api.get("filaProcessamento/processos").then((resp) => {
        this.processos = resp.data;
        this.isBusy = false;
        this.filterSize();
      });
    },
    filterSize() {
      let sum = 0;
      this.processos.map(x => {
        if (x.nome.toUpperCase().includes(this.filter.trim().toUpperCase())) {
          sum++;
        }
      })
      this.FilterSize = sum;
      return sum;
    },
    async pesquisar() {
      this.isBusy = !this.isBusy; //loading
      this.FindAll();
      this.isBusy = false;
      this.filterSize()
    },

    readForms() {
      api.get("filaProcessamento/fila").then((resp) => {
        // this.isBusy = false;
        this.fila = resp.data;
        // console.log(resp.data);
      });
    },
    formatarData: function (value) {
      if (value === null) {
        return null;
      }
      return new Date(value).toLocaleString("pt-BR", {
        year: "numeric",
        month: "2-digit",
        day: "2-digit",
        hour: "2-digit",
        minute: "2-digit",
        second: "2-digit",
      });
    },

    // FormatarStatus: function (value) {
    //   if (value === "ok") {
    //     return;
    //   }
    // },
    statusIcon(label) {
      if (label === "ok") return "check-square";
      else if (label === "mapear erro") return "x-circle";
    },
    rowClass(item, type) {
      if (!item || type !== "row") return;
      if (item.status === "mapear erro") return "check";
      else if (item.status === "ok") return "x";
    },
  },

  computed: {
    rows() {
      return this.processos;
    },
  },
};
</script>
<style >
.fonteLinha {
  font-size: 14px;
  text-align: center;
}

.fonteLinhaLeft {
  font-size: 14px;
}

.select-selected {
  border-color: black;
  border: 6px solid;
}

.select-selected.select-arrow-active:after {
  border-color: black;
  top: 7px;
}

.label-spacing{
  margin-top: 10px;
}

.align-right{
  text-align: right;
}

.spacing {
  margin-right: 20px;
}

.width-unidade {
  width: 1000px; /* Ajuste para o tamanho desejado */
}

.select-items div,
.select-selected {
  color: black;
  padding: 8px 16px;
  border: 1px solid;
  border-color: rgba(0, 0, 0, 0.2);
  cursor: pointer;
}

.select-items {
  position: absolute;

  top: 100%;
  left: 0;
  right: 0;
  z-index: 99;
}

.select-hide {
  display: none;
}

.pesquisa_select {
  position: relative;
  margin-top: 20px;
}

.select-items div:hover,
.same-as-selected {
  background-color: rgba(0, 0, 0, 0.1);
}

@mixin flex-center($columns: false) {
  display: flex;
  align-items: center;
  justify-content: center;

  @if $columns {
    flex-direction: column;
  }
}
</style>
